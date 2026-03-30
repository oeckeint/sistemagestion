package common.publisher.incident.publisher.model;

public enum Flow {

    AUTOCONSUMO_VALIDATION("Autoconsumo Validation");

    private String key;

    Flow(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
