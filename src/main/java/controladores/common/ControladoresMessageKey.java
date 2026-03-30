package controladores.common;

import common.i18n.MessageKey;

public enum ControladoresMessageKey implements MessageKey {

    XML_HELPER_TARIFA_FACTOR_INCONSISTENCY("controladores.xmlhelper.tarifa.factor.inconsistency"),
    XML_HELPER_AUTOCONSUMO_TYPE_12_WITHOUT_DETAIL("controladores.xmlhelper.autoconsumo.type.12.without.detail"),
    XML_HELPER_AUTOCONSUMO_UNKNOWN_TYPE_NO_DATA("controladores.xmlhelper.autoconsumo.unknown.type.no.data");

    private final String key;

    ControladoresMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

}
