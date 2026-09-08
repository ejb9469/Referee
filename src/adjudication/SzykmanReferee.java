package adjudication;

import domain.Order;
import domain.OrderType;
import domain.Province;
import util.Orders;

import java.util.*;

/**
 * A DATC-oriented Referee policy that resolves convoy paradoxes using the
 * Szykman rule / principle.
 *
 * <p>At this stage, this class retains Referee's ordinary candidate collection
 * and final-selection behavior, then applies the existing narrow single-convoy
 * Szykman interpretation used for 6.F.17.P.</p>
 *
 * <p>Future work belongs here rather than in {@code Referee} when it:</p>
 *
 * <ul>
 *     <li>identifies a contradictory convoy-dependent component;</li>
 *     <li>temporarily interprets the affected convoy orders as HOLD orders;</li>
 *     <li>re-adjudicates the transformed position; and</li>
 *     <li>projects the effective-HOLD result back onto the submitted convoy
 *         orders for reporting.</li>
 * </ul>
 *
 * @author Evan B
 */
public class SzykmanReferee extends Referee {


    // Constructors \\

    public SzykmanReferee() {
        this(
                Collections.emptyList(),
                NUM_TRIALS_DEFAULT,
                SHUFFLE_SEED_DEFAULT
        );
    }

    public SzykmanReferee(Collection<Order> orders) {
        this(
                orders,
                NUM_TRIALS_DEFAULT,
                SHUFFLE_SEED_DEFAULT
        );
    }

    public SzykmanReferee(int numTrials) {
        this(
                Collections.emptyList(),
                numTrials,
                SHUFFLE_SEED_DEFAULT
        );
    }

    public SzykmanReferee(
            Collection<Order> orders,
            int numTrials
    ) {
        this(
                orders,
                numTrials,
                SHUFFLE_SEED_DEFAULT
        );
    }

    /**
     * Creates a SzykmanReferee with a known seed, allowing reproducible trial
     * ordering during paradox investigation.
     *
     * @param orders orders to adjudicate
     * @param numTrials number of shuffled orderings to examine
     * @param shuffleSeed seed used to generate shuffled orderings
     */
    public SzykmanReferee(
            Collection<Order> orders,
            int numTrials,
            long shuffleSeed
    ) {
        super(orders, numTrials, shuffleSeed);
    }


    // Final meta-resolution selection \\

    /**
     * Applies the Szykman-specific convoy-paradox policy after Referee has
     * collected raw candidates.<br><br>
     *
     * A single conflicting convoy retains the narrow F17 compatibility rule.
     * Two or more conflicting convoys are replaced by HOLD orders together,
     * then the complete transformed position is adjudicated afresh.
     */
    @Override
    protected Collection<Order> selectFinalResolution() {

        /*
         * Referee.judge() restores this.orders to the submitted order set before
         * calling this hook. Preserve that state before the inherited selector
         * potentially performs its legacy tie-handling re-adjudication.
         */
        Collection<Order> submittedOrders = new ArrayList<>(
                Orders.deepCopy(this.orders)
        );

        Collection<Order> baseResolution =
                super.selectFinalResolution();

        if (baseResolution == null)
            return null;

        Map<String, Order> conflictingConvoys =
                this.findConflictingConvoys(this.resolutions.values());

        if (conflictingConvoys.size() >= 2) {
            return this.adjudicateWithConflictingConvoysHeld(
                    submittedOrders,
                    conflictingConvoys
            );
        }

        Set<Order> selectedResolution = new LinkedHashSet<>(
                Orders.deepCopy(baseResolution)
        );

        return this.applySingleConvoyParadoxRule(selectedResolution);

    }


    // Szykman convoy-paradox helpers \\

    /**
     * Applies the Szykman rule to a multi-convoy paradox.<br><br>
     *
     * Each submitted convoy whose outcome differs across raw candidates is
     * converted to a snapshot-backed HOLD before all orders are adjudicated
     * together. The conversion is simultaneous, so no conflicting raw
     * permutation is allowed to choose which convoy survives.
     */
    private Collection<Order> adjudicateWithConflictingConvoysHeld(
            Collection<Order> submittedOrders,
            Map<String, Order> conflictingConvoys
    ) {

        List<Order> transformedOrders = new ArrayList<>(
                Orders.deepCopy(submittedOrders)
        );

        for (Order order : transformedOrders) {

            if (order.orderType != OrderType.CONVOY)
                continue;

            String orderKey = orderIdentityKey(originalOrderOf(order));

            if (!conflictingConvoys.containsKey(orderKey))
                continue;

            order.takeSnapshot();
            order.orderType = OrderType.HOLD;
            order.pos1 = null;
            order.pos2 = null;
        }

        Judge judge = new Judge(transformedOrders);
        judge.judge();

        return new LinkedHashSet<>(
                Orders.deepCopy(judge.getOrders())
        );

    }

    /**
     * Applies the narrow one-conflicting-convoy resolution policy.<br><br>
     *
     * The policy is intentionally conservative:<br><br>
     *
     * 1. There must be exactly one conflicting convoy.
     * 2. The selected candidate must already resolve that convoy as failed.
     * 3. The associated convoyed army must already fail in the selected result.
     * 4. A successful move directly attacking the convoy fleet must have an
     *    unstable `resolved` state across raw candidates.<br><br>
     *
     * A matching move satisfying all conditions is forced to fail. This retains
     * the selected candidate's result for every unrelated order.
     */
    private Set<Order> applySingleConvoyParadoxRule(
            Set<Order> selectedResolution
    ) {

        Map<String, Order> conflictingConvoys =
                this.findConflictingConvoys(this.resolutions.values());

        if (conflictingConvoys.size() != 1)
            return selectedResolution;

        Order conflictingConvoy =
                conflictingConvoys.values().iterator().next();

        Order selectedConvoy = findMatchingOriginalOrder(
                conflictingConvoy,
                selectedResolution
        );

        if (selectedConvoy == null
                || selectedConvoy.verdict) {
            return selectedResolution;
        }

        Order correspondingMove = Orders.locateCorresponding(
                conflictingConvoy,
                selectedResolution
        );

        if (correspondingMove == null
                || correspondingMove.verdict) {
            return selectedResolution;
        }

        Set<Order> adjustedResolution = new LinkedHashSet<>(
                Orders.deepCopy(selectedResolution)
        );

        for (Order order : adjustedResolution) {

            if (order.orderType != OrderType.MOVE)
                continue;

            /*
             * Only direct attacks on the paradoxical convoy fleet are in scope.
             */
            if (!Province.equalsIgnoreCoast(
                    order.pos1,
                    conflictingConvoy.pos0
            )) {
                continue;
            }

            /*
             * The convoyed army is not attacking the convoy fleet's province
             * in the F17 shape, but exclude it explicitly for safety.
             */
            if (sameOriginalOrder(order, correspondingMove))
                continue;

            /*
             * A normal direct attack remains untouched. Only one whose
             * resolution bookkeeping differs across raw candidates belongs to
             * the contradictory convoy dependency.
             */
            if (!this.resolvedStateVariesAcrossCandidates(order))
                continue;

            order.resolved = true;
            order.verdict = false;
        }

        return adjustedResolution;

    }

    /**
     * Returns true if a submitted order's resolved/unresolved state differs
     * across every raw trial collected for every verdict-level candidate.<br><br>
     *
     * `resolutionKey(...)` intentionally ignores `resolved`; therefore this
     * method uses CandidateObservation's aggregate state rather than one
     * arbitrary first-discovered representative stored in `resolutions`.
     */
    private boolean resolvedStateVariesAcrossCandidates(Order order) {

        boolean observedResolved = false;
        boolean observedUnresolved = false;

        for (CandidateObservation observation :
                this.candidateObservations.values()) {

            if (observation.hasObservedResolvedState(order, true))
                observedResolved = true;

            if (observation.hasObservedResolvedState(order, false))
                observedUnresolved = true;

            if (observedResolved && observedUnresolved)
                return true;
        }

        return false;

    }


}