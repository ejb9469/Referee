package adjudication;

/**
 * A non-speculative ordinary adjudication result.
 *
 * <p>UNKNOWN means the result still depends on an unresolved order. Unlike
 * Judge's recursive resolver, the ordinary probe never substitutes an
 * optimistic or pessimistic guess for UNKNOWN.</p>
 */
public enum ResolutionState {

    SUCCESS,
    FAILURE,
    UNKNOWN;


    public boolean isKnown() {
        return this != UNKNOWN;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public static ResolutionState fromBoolean(boolean successful) {
        return successful ? SUCCESS : FAILURE;
    }

}