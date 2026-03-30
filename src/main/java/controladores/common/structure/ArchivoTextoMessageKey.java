package controladores.common.structure;

import common.i18n.MessageKey;

public enum ArchivoTextoMessageKey implements MessageKey {
    WRITE_ERROR_FAILED("archivo.texto.write.error.failed"),
    WRITE_WARNING_FAILED("archivo.texto.write.warning.failed"),
    WARNING_TEMPLATE("archivo.texto.warning.template"),
    WARNING_LINE_WITH_FILE("archivo.texto.warning.line.with.file"),
    WARNING_CODE_1("archivo.texto.warning.code.1"),
    WARNING_CODE_2("archivo.texto.warning.code.2"),
    WARNING_CODE_3("archivo.texto.warning.code.3"),
    WARNING_CODE_4("archivo.texto.warning.code.4"),
    WARNING_CODE_5("archivo.texto.warning.code.5"),
    WARNING_CODE_6("archivo.texto.warning.code.6"),
    WARNING_CODE_7("archivo.texto.warning.code.7"),
    WARNING_CODE_8("archivo.texto.warning.code.8"),
    WARNING_CODE_9("archivo.texto.warning.code.9"),
    WARNING_CODE_10("archivo.texto.warning.code.10"),
    WARNING_CODE_11("archivo.texto.warning.code.11"),
    WARNING_CODE_12("archivo.texto.warning.code.12"),
    WARNING_CODE_13("archivo.texto.warning.code.13"),
    WARNING_CODE_14("archivo.texto.warning.code.14"),
    WARNING_CODE_15("archivo.texto.warning.code.15"),
    WARNING_CODE_16("archivo.texto.warning.code.16"),
    WARNING_CODE_17("archivo.texto.warning.code.17"),
    WARNING_CODE_18("archivo.texto.warning.code.18"),
    WARNING_CODE_19("archivo.texto.warning.code.19"),
    WARNING_CODE_20("archivo.texto.warning.code.20"),
    WARNING_CODE_21("archivo.texto.warning.code.21"),
    WARNING_CODE_22("archivo.texto.warning.code.22"),
    WARNING_CODE_23("archivo.texto.warning.code.23"),
    WARNING_CODE_24("archivo.texto.warning.code.24"),
    WARNING_CODE_25("archivo.texto.warning.code.25"),
    WARNING_CODE_26("archivo.texto.warning.code.26"),
    WARNING_CODE_27("archivo.texto.warning.code.27"),
    WARNING_CODE_28("archivo.texto.warning.code.28"),
    WARNING_CODE_29("archivo.texto.warning.code.29"),
    WARNING_CODE_30("archivo.texto.warning.code.30"),
    WARNING_CODE_31("archivo.texto.warning.code.31"),
    WARNING_CODE_32("archivo.texto.warning.code.32"),
    WARNING_CODE_UNKNOWN("archivo.texto.warning.code.unknown");

    private final String key;

    ArchivoTextoMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

}
