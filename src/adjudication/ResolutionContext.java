package adjudication;

import domain.Order;

import java.util.Collections;
import java.util.IdentityHashMap;
//import java.util.Map;
import java.util.Set;

/**
 * Immutable, branch-local adjudication context.
 *
 * This class holds temporary assumptions that must not be written directly to
 * shared Order instances while Judge.resolve() is exploring recursive,
 * optimistic, and pessimistic branches.
 *
 * Order identity is intentional. Two distinct Order objects may compare equal
 * as submitted orders, but a context applies to the specific objects being
 * resolved in one Judge invocation.
 */
public final class ResolutionContext {

    private final Set<Order> suppressedHeadToHeadOrders;

    private ResolutionContext(Set<Order> suppressedHeadToHeadOrders) {
        this.suppressedHeadToHeadOrders = suppressedHeadToHeadOrders;
    }

    /**
     * Returns an empty context with no temporary assumptions.
     */
    public static ResolutionContext empty() {
        return new ResolutionContext(
                Collections.newSetFromMap(new IdentityHashMap<>())
        );
    }

    /**
     * Returns whether normal head-to-head adjudication is suppressed for an
     * order in this branch.
     */
    public boolean suppressesHeadToHead(Order order) {
        return this.suppressedHeadToHeadOrders.contains(order);
    }

    /**
     * Returns a new context in which both members of a convoy swap are treated
     * as non-head-to-head movers.
     *
     * The original context is not mutated.
     */
    public ResolutionContext withHeadToHeadSuppressed(
            Order first,
            Order second
    ) {
        Set<Order> copy = Collections.newSetFromMap(
                new IdentityHashMap<>()
        );

        copy.addAll(this.suppressedHeadToHeadOrders);
        copy.add(first);
        copy.add(second);

        return new ResolutionContext(copy);
    }

    /**
     * Returns a diagnostic-only count of the currently suppressed orders.
     */
    public int suppressedHeadToHeadCount() {
        return this.suppressedHeadToHeadOrders.size();
    }
}