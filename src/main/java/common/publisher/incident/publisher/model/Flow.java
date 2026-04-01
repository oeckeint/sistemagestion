package common.publisher.incident.publisher.model;

import lombok.Getter;

@Getter
public enum Flow {

    AUTOCONSUMO_VALIDATION("Autoconsumo Validation"),
    LEGACY_WARNING("Legacy Warning");

    private String key;

    Flow(String key) {
        this.key = key;
    }

}
