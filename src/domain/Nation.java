package domain;

public enum Nation {

    ENGLAND,
    FRANCE,
    GERMANY,
    ITALY,
    AUSTRIA,
    RUSSIA,
    TURKEY;


    public String getPrefix() {
        String name = this.name();
        if (name.length() < 2)
            return name.substring(0,1);  // 1 letter (keep case) or blank (impossible here -- throw exception)
        else
            return name.charAt(0)+name.substring(1,2).toLowerCase();
    }

}