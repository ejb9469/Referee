package domain;

public enum Season {

    SPRING,
    SPRING_RETREATS,
    FALL,
    FALL_RETREATS,
    WINTER;


    public static Season rotate(Season season, boolean retreats) {
        switch (season) {
            case SPRING -> {
                if (retreats)
                    return SPRING_RETREATS;
                else
                    return FALL;
            }
            case SPRING_RETREATS -> { return FALL; }
            case FALL -> {
                if (retreats)
                    return FALL_RETREATS;
                else
                    return WINTER;
            }
            case FALL_RETREATS -> { return WINTER; }
            case WINTER -> { return SPRING; }
            default ->
                throw new IllegalArgumentException("`Season.rotate(...)` supplied with faulty `season` argument");
        }
    }

}
