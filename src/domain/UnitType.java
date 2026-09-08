package domain;

public enum UnitType {

    ARMY,
    FLEET;


    public static UnitType valueOfMarker(char marker) {
        if (marker == 'A' || marker == 'a')
            return ARMY;
        if (marker == 'F' || marker == 'f')
            return FLEET;
        return null;
    }

}
