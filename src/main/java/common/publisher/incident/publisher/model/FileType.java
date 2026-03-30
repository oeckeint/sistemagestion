package common.publisher.incident.publisher.model;

public enum FileType {

    PEAJES("peajes"),
    FACTURAS("facturas"),
    OTRAS_FACTURAS("otras facturas"),
    UNKNOWN("unknown");

    private final String key;

    FileType(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

}
