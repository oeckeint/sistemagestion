package common.publisher.incident.publisher.model;

public enum DataKeys {

    TIPO_SUBSECCION,
    TIPO_AUTOCONSUMO;

    public String key() {
        return name().toLowerCase();
    }

}
