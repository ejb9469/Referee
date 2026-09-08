package adjudication;

/**
 * Result of one Judge.resolve() attempt.
 *
 * A successful value is not necessarily definitive. During recursive
 * dependency evaluation, Judge may return the caller's optimistic or
 * pessimistic guess while leaving the Order unresolved.
 */
public final class ResolutionResult {

    private final boolean successful;
    private final boolean definitive;

    private ResolutionResult(boolean successful, boolean definitive) {
        this.successful = successful;
        this.definitive = definitive;
    }

    /**
     * Creates a result for an order whose value has been definitively
     * established and committed to Order.resolved / Order.verdict.
     */
    public static ResolutionResult definitive(boolean successful) {
        return new ResolutionResult(successful, true);
    }

    /**
     * Creates a result returned while recursive resolution remains uncertain.
     *
     * The value is usable by the current speculative calculation, but must not
     * be treated as a persisted final resolution.
     */
    public static ResolutionResult provisional(boolean successful) {
        return new ResolutionResult(successful, false);
    }

    /**
     * The successful/failed value observed by the caller.
     */
    public boolean isSuccessful() {
        return this.successful;
    }

    /**
     * Whether this result was definitive rather than a recursive guess.
     */
    public boolean isDefinitive() {
        return this.definitive;
    }

    @Override
    public String toString() {
        return "ResolutionResult[successful="
                + this.successful
                + ", definitive="
                + this.definitive
                + "]";
    }
}