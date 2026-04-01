package common.documentstructure;

import common.publisher.common.XmlNodeKey;

public enum FacturaAtrNodes implements XmlNodeKey {

    TIPO_SUBSECCION("TipoSubseccion");

    private final String value;

    FacturaAtrNodes(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
