package common.documentstructure;

import common.publisher.common.XmlNodeKey;

public enum AutoconsumoNodes implements XmlNodeKey {

    ENERGIA_NETA_GENERADA("EnergiaNetaGen"),
    ENERGIA_AUTOCONSUMIDA("EnergiaAutoconsumida"),

    TIPO_AUTOCONSUMO("tipoAutoconsumo"),
    TIPO_SUBSECCION("tipoSubseccion");

    private final String value;

    AutoconsumoNodes(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
