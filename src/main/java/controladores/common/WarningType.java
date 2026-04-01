package controladores.common;

import controladores.common.structure.ArchivoTextoMessageKey;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public enum WarningType {

    // --- Potencia ---
    POTENCIA_CONTRATADA_EXCEDE_PERIODOS         ("1",  ArchivoTextoMessageKey.WARNING_CODE_1),
    POTENCIA_MAX_DEMANDADA_EXCEDE_PERIODOS      ("2",  ArchivoTextoMessageKey.WARNING_CODE_2),
    POTENCIA_A_FACTURAR_EXCEDE_PERIODOS         ("3",  ArchivoTextoMessageKey.WARNING_CODE_3),
    PRECIO_POTENCIA_EXCEDE_PERIODOS             ("4",  ArchivoTextoMessageKey.WARNING_CODE_4),
    IMPORTE_TOTAL_POTENCIA_DUPLICADO            ("5",  ArchivoTextoMessageKey.WARNING_CODE_5),

    // --- Energía Activa ---
    ENERGIA_ACTIVA_FECHAS_EXCEDIDAS             ("6",  ArchivoTextoMessageKey.WARNING_CODE_6),
    VALOR_ENERGIA_ACTIVA_EXCEDE_PERIODOS        ("7",  ArchivoTextoMessageKey.WARNING_CODE_7),
    PRECIO_ENERGIA_EXCEDE_PERIODOS              ("8",  ArchivoTextoMessageKey.WARNING_CODE_8),
    IMPORTE_TOTAL_ENERGIA_ACTIVA_DUPLICADO      ("9",  ArchivoTextoMessageKey.WARNING_CODE_9),

    // --- Impuestos y cargos fijos ---
    IMPORTE_IMPUESTO_ELECTRICO_DUPLICADO        ("10", ArchivoTextoMessageKey.WARNING_CODE_10),
    IMPORTE_FACTURACION_ALQUILERES_DUPLICADO    ("11", ArchivoTextoMessageKey.WARNING_CODE_11),
    BASE_IMPONIBLE_DUPLICADA                    ("12", ArchivoTextoMessageKey.WARNING_CODE_12),

    // --- Lecturas AE ---
    CONSUMO_CALCULADO_AE_EXCEDE_PERIODOS        ("13", ArchivoTextoMessageKey.WARNING_CODE_13),
    LECTURA_DESDE_AE_EXCEDE_PERIODOS            ("14", ArchivoTextoMessageKey.WARNING_CODE_14),
    LECTURA_HASTA_AE_EXCEDE_PERIODOS            ("15", ArchivoTextoMessageKey.WARNING_CODE_15),

    // --- Lecturas R ---
    CONSUMO_CALCULADO_R_EXCEDE_PERIODOS         ("16", ArchivoTextoMessageKey.WARNING_CODE_16),
    LECTURA_DESDE_R_EXCEDE_PERIODOS             ("17", ArchivoTextoMessageKey.WARNING_CODE_17),
    LECTURA_HASTA_R_EXCEDE_PERIODOS             ("18", ArchivoTextoMessageKey.WARNING_CODE_18),

    // --- Lecturas PM ---
    CONSUMO_CALCULADO_PM_EXCEDE_PERIODOS        ("19", ArchivoTextoMessageKey.WARNING_CODE_19),
    LECTURA_HASTA_PM_EXCEDE_PERIODOS            ("20", ArchivoTextoMessageKey.WARNING_CODE_20),

    // --- Tarifa e importes generales ---
    TARIFA_CLIENTE_NO_COINCIDE                  ("21", ArchivoTextoMessageKey.WARNING_CODE_21),
    IMPORTE_TOTAL_POTENCIA_NO_COINCIDE          ("22", ArchivoTextoMessageKey.WARNING_CODE_22),
    CARGOS_TIPO_01_EXCEDEN_LIMITE               ("23", ArchivoTextoMessageKey.WARNING_CODE_23),
    CARGOS_TIPO_02_EXCEDEN_LIMITE               ("24", ArchivoTextoMessageKey.WARNING_CODE_24),
    TOTAL_IMPORTE_CARGO_01_DUPLICADO            ("25", ArchivoTextoMessageKey.WARNING_CODE_25),
    TOTAL_IMPORTE_CARGO_02_DUPLICADO            ("26", ArchivoTextoMessageKey.WARNING_CODE_26),

    // --- Energía Excedentaria ---
    ENERGIA_EXCEDENTARIA_EXCEDE_VALORES         ("27", ArchivoTextoMessageKey.WARNING_CODE_27),
    ENERGIA_EXCEDENTARIA_TOTAL_NO_COINCIDE      ("28", ArchivoTextoMessageKey.WARNING_CODE_28),

    // --- Fechas Termino Potencia ---
    TERMINO_POTENCIA_FECHAS_EXCEDIDAS           ("29", ArchivoTextoMessageKey.WARNING_CODE_29),

    // --- Autoconsumo ---
    AUTOCONSUMO_DUPLICADO                       ("30", ArchivoTextoMessageKey.WARNING_CODE_30),
    AUTOCONSUMO_TIPO_12_SIN_DETALLE             ("31", ArchivoTextoMessageKey.WARNING_CODE_31),
    AUTOCONSUMO_NODOS_ESPERADOS_NO_ENCONTRADOS  ("32", ArchivoTextoMessageKey.WARNING_CODE_32),
    AUTOCONSUMO_TIPO_DESCONOCIDO                ("33", ArchivoTextoMessageKey.WARNING_CODE_UNKNOWN); // ⚠️ faltaba en el mapa

    private final String code;
    private final ArchivoTextoMessageKey messageKey;

    private static final Map<String, WarningType> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(w -> w.code, w -> w));

    WarningType(String code, ArchivoTextoMessageKey messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    /** Fallback controlado por el caller — más flexible que un Optional */
    public static WarningType fromCodeOr(String code, WarningType fallback) {
        if (code == null) return fallback;
        return Optional.ofNullable(BY_CODE.get(code))
                .orElse(fallback);
    }

    public static WarningType fromCodeOrDefault(String code) {
        return Optional.ofNullable(BY_CODE.get(code))
                .orElse(AUTOCONSUMO_TIPO_DESCONOCIDO);
    }

}
