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
import java.util.Locale;
import java.util.Map;
import org.springframework.context.ApplicationContext;

@Slf4j
public class ArchivoTexto {

    private static final ControladoresMessagesLogger CONTROLADORES_MESSAGES_LOGGER = new ControladoresMessagesLogger();
    private static final String PEAJES_PROPERTY = "peajes";
    private static final String DEFAULT_PEAJES_PATH = System.getProperty("user.home") + "/Downloads/com4energy/Peajes/";
    private static final String ERROR_PREFIX = "el archivo";
    private static final String ERROR_SUFFIX = "no se proceso porque";
    private static final int INDEX_NOT_FOUND = -1;
    private static final int MAX_WARNING_CODE = 32;
    private static final String UNKNOWN_WARNING_CODE = "33";
    private static final Map<String, ArchivoTextoMessageKey> WARNING_KEYS_BY_CODE = buildWarningKeys();

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
            return getUnknownFileName();
        }

        String normalized = errorLine.toLowerCase(Locale.ROOT);
        int prefixIndex = normalized.indexOf(ERROR_PREFIX);
        if (prefixIndex == INDEX_NOT_FOUND) {
            return getUnknownFileName();
        }

        int startIndex = prefixIndex + ERROR_PREFIX.length();
        int suffixIndex = normalized.indexOf(ERROR_SUFFIX, startIndex);
        if (suffixIndex == INDEX_NOT_FOUND || suffixIndex <= startIndex) {
            return getUnknownFileName();
        }

        String candidate = errorLine.substring(startIndex, suffixIndex).trim();
        return candidate.isEmpty()
                ? getUnknownFileName()
                : candidate;
    }

    private static String getUnknownFileName() {
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
        for (int code = 1; code <= MAX_WARNING_CODE; code++) {
            warningKeys.put(String.valueOf(code), ArchivoTextoMessageKey.valueOf("WARNING_CODE_" + code));
        }
        warningKeys.put(UNKNOWN_WARNING_CODE, ArchivoTextoMessageKey.WARNING_CODE_UNKNOWN);
        return warningKeys;
    }

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

}
