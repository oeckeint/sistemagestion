package utileria;

import app.config.ApplicationContextUtils;
import common.i18n.Messages;
import common.publisher.incident.publisher.IncidentPublisherServiceBridge;
import common.publisher.incident.publisher.model.FileType;
import common.publisher.incident.publisher.model.Flow;
import common.publisher.incident.publisher.model.TechnicalContextResolver;
import common.publisher.incident.publisher.model.DocumentWarning;
import controladores.common.ControladoresMessagesLogger;
import controladores.common.HTTPMethod;
import controladores.common.Paths;
import controladores.common.WarningType;
import controladores.common.structure.ArchivoTextoMessageKey;
import controladores.helper.Utilidades;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.ApplicationContext;

@Slf4j
public class ArchivoTexto {

    private static final ControladoresMessagesLogger CONTROLADORES_MESSAGES_LOGGER = new ControladoresMessagesLogger();
    private static final String PEAJES_PROPERTY = "peajes";
    private static final String DEFAULT_PEAJES_PATH =
            System.getProperty("user.home") + "/Downloads/com4energy/Peajes/";
    private static final Map<String, ArchivoTextoMessageKey> WARNING_KEYS_BY_CODE = buildWarningKeys();
    private static final Pattern FILE_NAME_FROM_ERROR_PATTERN =
            Pattern.compile("El archivo\\s+(.+?)\\s+no se proceso porque", Pattern.CASE_INSENSITIVE);

    private enum PeajesLogFile {
        ERRORES("errores.txt"),
        ADVERTENCIAS("advertencias.txt");

        private final String fileName;

        PeajesLogFile(String fileName) {
            this.fileName = fileName;
        }

        public String fileName() {
            return fileName;
        }
    }

    public static void escribirError(String contenido) {
        String formateoContenido = limpiarEtiquetasResaltado(contenido);
        appendLineInPeajesFile(
                PeajesLogFile.ERRORES,
                formateoContenido,
                ArchivoTextoMessageKey.WRITE_ERROR_FAILED
        );

        // Legacy: errores de procesamiento también se publican como incidentes documentales
        // para tener trazabilidad en DB aunque el flujo original sea TXT.
        publishLegacyProcessingError(formateoContenido);
    }

    public static void escribirAdvertencia(String nombreArchivo, String codAdvertencia) {
        escribirAdvertencia(nombreArchivo, codAdvertencia, true);
    }

    private static void escribirAdvertencia(String nombreArchivo, String codAdvertencia, boolean publishToIncident) {
        String warningMessage = identificarAdvertencia(codAdvertencia);
        String warningLine = Messages.format(ArchivoTextoMessageKey.WARNING_LINE_WITH_FILE, warningMessage, nombreArchivo);

        appendLineInPeajesFile(PeajesLogFile.ADVERTENCIAS, warningLine, ArchivoTextoMessageKey.WRITE_WARNING_FAILED);

        if (publishToIncident) {
            publishLegacyWarning(nombreArchivo, codAdvertencia, warningLine);
        }

    }

    public static void publishWarning(DocumentWarning warning) {
        // Evita doble publicación en DB: ya tenemos el DocumentWarning completo.
        escribirAdvertencia(warning.getFileName(), warning.resolvedCode(), false);
        IncidentPublisherServiceBridge.publishDocumentWarning(warning);
    }

    private static void publishLegacyWarning(String fileName, String code, String warningLine) {
        WarningType warningType = WarningType.fromCodeOrDefault(code);

        DocumentWarning warning = DocumentWarning.builder()
                .warning(warningType)
                .messageOverride(warningLine)
                .endpoint(Paths.Procesamiento.PROCESAR)
                .httpMethod(HTTPMethod.POST)
                .fileName(fileName)
                .fileType(FileType.UNKNOWN)
                .flow(Flow.LEGACY_WARNING)
                .technicalContext(TechnicalContextResolver.resolveTyped())
                .build();

        IncidentPublisherServiceBridge.publishDocumentWarning(warning);
    }

    private static void publishLegacyProcessingError(String errorLine) {
        String fileName = extractFileNameFromErrorLine(errorLine);

        DocumentWarning warning = DocumentWarning.builder()
                .warning(WarningType.AUTOCONSUMO_TIPO_DESCONOCIDO)
                .messageOverride(errorLine)
                .endpoint(Paths.Procesamiento.PROCESAR)
                .httpMethod(HTTPMethod.POST)
                .fileName(fileName)
                .fileType(FileType.UNKNOWN)
                .flow(Flow.LEGACY_WARNING)
                .technicalContext(TechnicalContextResolver.resolveTyped())
                .build();

        IncidentPublisherServiceBridge.publishDocumentWarning(warning);
    }

    private static String extractFileNameFromErrorLine(String errorLine) {
        if (errorLine == null || errorLine.trim().isEmpty()) {
            return Messages.get(ArchivoTextoMessageKey.FILE_NAME_UNKNOWN);
        }

        Matcher matcher = FILE_NAME_FROM_ERROR_PATTERN.matcher(errorLine);
        if (matcher.find()) {
            String candidate = matcher.group(1);
            return candidate != null && !candidate.trim().isEmpty()
                    ? candidate.trim()
                    : Messages.get(ArchivoTextoMessageKey.FILE_NAME_UNKNOWN);
        }
        return Messages.get(ArchivoTextoMessageKey.FILE_NAME_UNKNOWN);
    }

    private static String momentoActual() {
        return new SimpleDateFormat("<dd/MM/yyyy HH:mm:ss> ").format(new Date());
    }

    private static String identificarAdvertencia(String cod) {
        String safeCode = cod == null ? "" : cod;
        ArchivoTextoMessageKey warningKey = WARNING_KEYS_BY_CODE.getOrDefault(
                safeCode,
                ArchivoTextoMessageKey.WARNING_CODE_UNKNOWN
        );
        return Messages.format(
                ArchivoTextoMessageKey.WARNING_TEMPLATE,
                safeCode,
                Messages.get(warningKey)
        );
    }

    private static String limpiarEtiquetasResaltado(String contenido) {
        return contenido.replace("<Strong>", "").replace("</Strong>", "");
    }


    private static void appendLineInPeajesFile(PeajesLogFile logFile, String line, ArchivoTextoMessageKey errorKey) {
        Path peajesPath = resolvePeajesPath();
        Utilidades.creacionDirectorios(peajesPath.toString());
        File file = peajesPath.resolve(logFile.fileName()).toFile();
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            if (file.length() == 0) {
                bw.write(momentoActual() + line);
            } else {
                bw.write("\n" + momentoActual() + line);
            }
        } catch (IOException e) {
            CONTROLADORES_MESSAGES_LOGGER.warn(log, Messages.get(errorKey));
        }
    }

    private static Path resolvePeajesPath() {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        if (context != null) {
            String configuredPath = context.getEnvironment().getProperty(PEAJES_PROPERTY);
            if (configuredPath != null && !configuredPath.trim().isEmpty()) {
                return java.nio.file.Paths.get(configuredPath.trim()).normalize().toAbsolutePath();
            }
        }
        return java.nio.file.Paths.get(DEFAULT_PEAJES_PATH).normalize().toAbsolutePath();
    }

    private static Map<String, ArchivoTextoMessageKey> buildWarningKeys() {
        Map<String, ArchivoTextoMessageKey> warningKeys = new HashMap<>();
        warningKeys.put("1", ArchivoTextoMessageKey.WARNING_CODE_1);
        warningKeys.put("2", ArchivoTextoMessageKey.WARNING_CODE_2);
        warningKeys.put("3", ArchivoTextoMessageKey.WARNING_CODE_3);
        warningKeys.put("4", ArchivoTextoMessageKey.WARNING_CODE_4);
        warningKeys.put("5", ArchivoTextoMessageKey.WARNING_CODE_5);
        warningKeys.put("6", ArchivoTextoMessageKey.WARNING_CODE_6);
        warningKeys.put("7", ArchivoTextoMessageKey.WARNING_CODE_7);
        warningKeys.put("8", ArchivoTextoMessageKey.WARNING_CODE_8);
        warningKeys.put("9", ArchivoTextoMessageKey.WARNING_CODE_9);
        warningKeys.put("10", ArchivoTextoMessageKey.WARNING_CODE_10);
        warningKeys.put("11", ArchivoTextoMessageKey.WARNING_CODE_11);
        warningKeys.put("12", ArchivoTextoMessageKey.WARNING_CODE_12);
        warningKeys.put("13", ArchivoTextoMessageKey.WARNING_CODE_13);
        warningKeys.put("14", ArchivoTextoMessageKey.WARNING_CODE_14);
        warningKeys.put("15", ArchivoTextoMessageKey.WARNING_CODE_15);
        warningKeys.put("16", ArchivoTextoMessageKey.WARNING_CODE_16);
        warningKeys.put("17", ArchivoTextoMessageKey.WARNING_CODE_17);
        warningKeys.put("18", ArchivoTextoMessageKey.WARNING_CODE_18);
        warningKeys.put("19", ArchivoTextoMessageKey.WARNING_CODE_19);
        warningKeys.put("20", ArchivoTextoMessageKey.WARNING_CODE_20);
        warningKeys.put("21", ArchivoTextoMessageKey.WARNING_CODE_21);
        warningKeys.put("22", ArchivoTextoMessageKey.WARNING_CODE_22);
        warningKeys.put("23", ArchivoTextoMessageKey.WARNING_CODE_23);
        warningKeys.put("24", ArchivoTextoMessageKey.WARNING_CODE_24);
        warningKeys.put("25", ArchivoTextoMessageKey.WARNING_CODE_25);
        warningKeys.put("26", ArchivoTextoMessageKey.WARNING_CODE_26);
        warningKeys.put("27", ArchivoTextoMessageKey.WARNING_CODE_27);
        warningKeys.put("28", ArchivoTextoMessageKey.WARNING_CODE_28);
        warningKeys.put("29", ArchivoTextoMessageKey.WARNING_CODE_29);
        warningKeys.put("30", ArchivoTextoMessageKey.WARNING_CODE_30);
        warningKeys.put("31", ArchivoTextoMessageKey.WARNING_CODE_31);
        warningKeys.put("32", ArchivoTextoMessageKey.WARNING_CODE_32);
        warningKeys.put("33", ArchivoTextoMessageKey.WARNING_CODE_UNKNOWN);
        return warningKeys;
    }
}

