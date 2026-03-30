package common.publisher.incident.contract;

public enum IncidentType {

    VALIDATION("validation"),
    INTEGRATION("integration"),
    SYSTEM("system");

    private final String key;

    IncidentType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static IncidentType fromKey(String value) {
        if (value == null) {
            return SYSTEM;
        }
        for (IncidentType type : values()) {
            if (type.key.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return SYSTEM;
    }

}
