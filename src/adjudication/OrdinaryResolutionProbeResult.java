package adjudication;

import domain.Order;
import util.Orders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of an ordinary, non-speculative resolution pass.
 */
public final class OrdinaryResolutionProbeResult {

    private final Map<String, ResolutionState> states;
    private final List<List<Order>> unresolvedComponents;


    public OrdinaryResolutionProbeResult(
            Map<String, ResolutionState> states,
            List<List<Order>> unresolvedComponents
    ) {
        this.states = Collections.unmodifiableMap(
                new LinkedHashMap<>(states)
        );

        List<List<Order>> copiedComponents = new ArrayList<>();

        for (List<Order> component : unresolvedComponents) {
            copiedComponents.add(new ArrayList<>(
                    Orders.deepCopy(component)
            ));
        }

        this.unresolvedComponents = Collections.unmodifiableList(
                copiedComponents
        );
    }


    public ResolutionState stateOf(Order order) {
        return this.states.getOrDefault(
                OrdinaryResolutionProbe.orderKey(order),
                ResolutionState.UNKNOWN
        );
    }

    public Map<String, ResolutionState> getStates() {
        return this.states;
    }

    public List<List<Order>> getUnresolvedComponents() {
        return this.unresolvedComponents;
    }

    public boolean isComplete() {
        return this.unresolvedComponents.isEmpty();
    }

}