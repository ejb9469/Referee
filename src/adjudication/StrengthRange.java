package adjudication;

/**
 * Inclusive lower and upper bounds for an order's possible strength while
 * some supporting orders remain unresolved.
 */
public record StrengthRange(int minimum, int maximum) {

    public StrengthRange {
        if (minimum < 0)
            throw new IllegalArgumentException(
                    "minimum strength cannot be negative"
            );
        if (maximum < minimum)
            throw new IllegalArgumentException(
                    "maximum strength cannot be less than minimum strength"
            );
    }

    /**
     * Returns true when this attack can never exceed the supplied defense.
     */
    public boolean cannotBeat(StrengthRange defense) {
        return this.maximum <= defense.minimum;
    }

    /**
     * Returns true when this attack exceeds the supplied defense in every
     * possible completion of currently unresolved dependencies.
     */
    public boolean alwaysBeats(StrengthRange defense) {
        return this.minimum > defense.maximum;
    }

}