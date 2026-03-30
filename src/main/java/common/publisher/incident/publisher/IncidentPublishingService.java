package common.publisher.incident.publisher;

import common.i18n.Messages;
import common.publisher.incident.publisher.model.CallerMethodResolveraa;
import common.publisher.common.Environment;
import common.publisher.common.Publisher;
import common.publisher.incident.contract.IncidentCategory;
import common.publisher.incident.contract.IncidentEvent;
import common.publisher.incident.contract.IncidentSeverity;
import common.publisher.incident.contract.IncidentStatus;
import common.publisher.incident.contract.IncidentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import common.publisher.incident.publisher.model.DocumentWarning;
import common.publisher.incident.publisher.model.TechnicalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicacion para construir y publicar incidentes en RabbitMQ.
 *
 * <p>Usar esta clase desde componentes Spring mediante inyeccion.
 * Para codigo legacy estatico, usar {@link IncidentPublisherServiceBridge}.</p>
 */
@Service
public class IncidentPublishingService {

    private static final Logger log = LoggerFactory.getLogger(IncidentPublishingService.class);
    private static final int MAX_METADATA_LENGTH = 1000;

    private final ObjectProvider<Publisher> publisherProvider;
    private final ObjectMapper objectMapper;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${app.incident-publisher.service-name:sistemagestion}")
    private String serviceName;

    @Value("${app.incident-publisher.environment:DEV}")
    private String environment;

    @Value("${app.incident-publisher.default-type:system}")
    private String defaultType;

    public IncidentPublishingService(
            @Qualifier("incidentPublisher") ObjectProvider<Publisher> publisherProvider,
            @Qualifier("incidentObjectMapper") ObjectMapper objectMapper
    ) {
        this.publisherProvider = publisherProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * Publica un incidente de tipo SYSTEM con metadata basica del origen.
     *
     * @param message detalle del error
     * @param methodName metodo o punto de origen
     */
    public void publishSystemError(String message, String methodName) {
        publishSystemError(message, methodName, null);
    }

    /**
     * Publica un incidente SYSTEM usando origen estructurado explicito.
     *
     * @param message detalle del error
     * @param callerInfo origen real capturado por el invocador
     */
    public void publishSystemError(String message, CallerMethodResolveraa.CallerInfo callerInfo) {
        publishSystemError(message, callerInfo, null);
    }

    /**
     * Publica un incidente SYSTEM usando origen estructurado explicito y
     * metadata adicional para diagnostico.
     *
     * @param message detalle del error
     * @param callerInfo origen real capturado por el invocador
     * @param additionalMetadata metadata adicional para diagnostico
     */
    public void publishSystemError(
            String message,
            CallerMethodResolveraa.CallerInfo callerInfo,
            Map<String, Object> additionalMetadata
    ) {
        Publisher publisher = publisherProvider.getIfAvailable();
        if (publisher == null) {
            return;
        }

        CallerMethodResolveraa.CallerInfo safeCallerInfo = callerInfo != null
                ? callerInfo
                : CallerMethodResolveraa.resolveCallerInfo(IncidentPublishingService.class);
        String resolvedMethodName = sanitize(safeCallerInfo.displayName());

        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName(serviceName)
                .environment(parseEnvironment(environment))
                .endpoint("sistemagestion")
                .methodName(resolvedMethodName)
                .exceptionType("SistemaGestionException")
                .message(sanitize(message))
                .category(IncidentCategory.SYSTEM)
                .severity(IncidentSeverity.ERROR)
                .status(IncidentStatus.NEW)
                .errorCode("SGE-ERROR")
                .metadata(buildErrorMetadata(resolvedMethodName, safeCallerInfo, additionalMetadata))
                .createdBy("sistemagestion")
                .timestamp(Instant.now())
                .build();

        try {
            publisher.send(IncidentType.fromKey(defaultType), event);
        } catch (Exception ex) {
            log.warn("No fue posible publicar incidente en RabbitMQ: {}", ex.getMessage());
        }
    }

    /**
     * Publica un incidente de tipo SYSTEM con metadata adicional estructurada.
     *
     * @param message detalle del error
     * @param methodName metodo o punto de origen
     * @param additionalMetadata metadata adicional para diagnostico
     */
    public void publishSystemError(String message, String methodName, Map<String, Object> additionalMetadata) {
        Publisher publisher = publisherProvider.getIfAvailable();
        if (publisher == null) {
            return;
        }

        String safeMethodName = sanitize(methodName);

        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName(serviceName)
                .environment(parseEnvironment(environment))
                .endpoint("sistemagestion")
                .methodName(safeMethodName)
                .exceptionType("SistemaGestionException")
                .message(sanitize(message))
                .category(IncidentCategory.SYSTEM)
                .severity(IncidentSeverity.ERROR)
                .status(IncidentStatus.NEW)
                .errorCode("SGE-ERROR")
                .metadata(buildErrorMetadata(safeMethodName, null, additionalMetadata))
                .createdBy("sistemagestion")
                .timestamp(Instant.now())
                .build();

        try {
            publisher.send(IncidentType.fromKey(defaultType), event);
        } catch (Exception ex) {
            log.warn("No fue posible publicar incidente en RabbitMQ: {}", ex.getMessage());
        }
    }

    /**
     * Mantiene compatibilidad para invocaciones que no proveen methodName.
     *
     * @param message detalle del error
     */
    public void publishSystemError(String message) {
        publishSystemError(message, (Map<String, Object>) null);
    }

    /**
     * Publica un incidente SYSTEM resolviendo dinamicamente el caller y adjuntando metadata adicional.
     *
     * @param message detalle del error
     * @param additionalMetadata metadata adicional para diagnostico
     */
    public void publishSystemError(String message, Map<String, Object> additionalMetadata) {
        Publisher publisher = publisherProvider.getIfAvailable();
        if (publisher == null) {
            return;
        }

        CallerMethodResolveraa.CallerInfo callerInfo =
                CallerMethodResolveraa.resolveCallerInfo(IncidentPublishingService.class);
        String resolvedMethodName = sanitize(callerInfo.displayName());

        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName(serviceName)
                .environment(parseEnvironment(environment))
                .endpoint("sistemagestion")
                .methodName(resolvedMethodName)
                .exceptionType("SistemaGestionException")
                .message(sanitize(message))
                .category(IncidentCategory.SYSTEM)
                .severity(IncidentSeverity.ERROR)
                .status(IncidentStatus.NEW)
                .errorCode("SGE-ERROR")
                .metadata(buildErrorMetadata(resolvedMethodName, callerInfo, additionalMetadata))
                .createdBy("sistemagestion")
                .timestamp(Instant.now())
                .build();

        try {
            publisher.send(IncidentType.fromKey(defaultType), event);
        } catch (Exception ex) {
            log.warn("No fue posible publicar incidente en RabbitMQ: {}", ex.getMessage());
        }
    }

    public void publishDocumentWarning(DocumentWarning warning) {

        Publisher publisher = publisherProvider.getIfAvailable();

        if (publisher == null || warning == null) {
            return;
        }

        TechnicalContext ctx = warning.getTechnicalContext();

        String methodName = ctx != null
                ? sanitize(ctx.getClassName() + "." + ctx.getMethod())
                : "UNKNOWN";

        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName(serviceName)
                .environment(parseEnvironment(environment))
                .endpoint("sistemagestion")

                // origen técnico
                .methodName(methodName)

                // semántica
                .exceptionType("DocumentWarning")
                .message(buildMessage(warning))
                .category(IncidentCategory.VALIDATION)
                .severity(IncidentSeverity.WARN)
                .status(IncidentStatus.NEW)

                .errorCode("SGE-WARN-" + sanitize(warning.resolvedCode()))

                // contexto archivo
                .filename(sanitize(warning.getFileName()))
                .fileType(resolveFileType(warning.getFileName(), defaultType))

                // metadata completa
                .metadata(buildMetadataJson(warning))

                .createdBy("sistemagestion")
                .timestamp(Instant.now())
                .build();

        try {
            publisher.send(IncidentType.VALIDATION, event);
        } catch (Exception ex) {
            log.warn("No fue posible publicar advertencia documental en RabbitMQ: {}", ex.getMessage());
        }
    }

    private Map<String, Object> buildMetadata(DocumentWarning warning) {

        Map<String, Object> metadata = new HashMap<String, Object>();

        metadata.put("code", sanitize(warning.resolvedCode()));
        metadata.put("flow", sanitize(String.valueOf(warning.getFlow())));

        // data de negocio
        if (warning.getData() != null) {
            metadata.put("data", warning.getData());
        }

        // contexto técnico
        TechnicalContext ctx = warning.getTechnicalContext();

        if (ctx != null) {
            Map<String, Object> tech = new HashMap<String, Object>();
            tech.put("class", ctx.getClassName());
            tech.put("method", ctx.getMethod());
            tech.put("line", ctx.getLine());
            tech.put("thread", ctx.getThread());
            tech.put("strategy", ctx.getResolutionStrategy().name());

            metadata.put("technicalContext", tech);
        }

        return metadata;
    }

    private String buildMessage(DocumentWarning warning) {
        String base = warning.getMessageOverride() != null
                ? warning.getMessageOverride()
                : Messages.get(warning.resolvedMessageKey());

        return sanitize(base) + " en el archivo " + sanitize(warning.getFileName());
    }


    /**
     * Publica una advertencia de documento con trazabilidad de archivo y codigo.
     *
     * @param fileName nombre del archivo asociado
     * @param warningCode codigo de advertencia funcional
     * @param warningMessage texto de advertencia ya formateado
     */
    public void publishDocumentWarning(String fileName, String warningCode, String warningMessage) {
        CallerMethodResolveraa.CallerInfo callerInfo =
                CallerMethodResolveraa.resolveCallerInfo(IncidentPublishingService.class);
        publishDocumentWarning(
                fileName,
                warningCode,
                warningMessage,
                callerInfo.displayName(),
                callerInfo,
                IncidentOrigin.DOCUMENT_WARNING_DYNAMIC
        );
    }

    /**
     * Publica una advertencia de documento con trazabilidad de archivo y codigo.
     *
     * @param fileName nombre del archivo asociado
     * @param warningCode codigo de advertencia funcional
     * @param warningMessage texto de advertencia ya formateado
     * @param methodName metodo o punto de origen
     */
    public void publishDocumentWarning(
            String fileName,
            String warningCode,
            String warningMessage,
            String methodName
    ) {
        publishDocumentWarning(
                fileName,
                warningCode,
                warningMessage,
                methodName,
                null,
                IncidentOrigin.DOCUMENT_WARNING_STATIC
        );
    }

    public void publishDocumentWarning(
            String fileName,
            String warningCode,
            String warningMessage,
            CallerMethodResolveraa.CallerInfo callerInfo
    ) {
        publishDocumentWarning(
                fileName,
                warningCode,
                warningMessage,
                callerInfo == null ? null : callerInfo.displayName(),
                callerInfo,
                IncidentOrigin.DOCUMENT_WARNING_DYNAMIC
        );
    }

    private void publishDocumentWarning(
            String fileName,
            String warningCode,
            String warningMessage,
            String methodName,
            CallerMethodResolveraa.CallerInfo callerInfo,
            IncidentOrigin incidentOrigin
    ) {
        Publisher publisher = publisherProvider.getIfAvailable();
        if (publisher == null) {
            return;
        }

        String safeFileName = sanitize(fileName);
        String safeWarningCode = sanitize(warningCode);
        String safeWarningMessage = sanitize(warningMessage);
        CallerMethodResolveraa.CallerInfo resolvedCallerInfo = resolveWarningCallerInfo(methodName, callerInfo);
        String resolvedMethodName = resolvedCallerInfo != null
                ? sanitize(resolvedCallerInfo.displayName())
                : sanitize(methodName);
        IncidentOrigin resolvedOrigin = resolvedCallerInfo != null
                ? IncidentOrigin.DOCUMENT_WARNING_DYNAMIC
                : incidentOrigin;

        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName(serviceName)
                .environment(parseEnvironment(environment))
                .endpoint("sistemagestion")
                .methodName(resolvedMethodName)
                .exceptionType("DocumentWarning")
                .message(safeWarningMessage + " en el archivo " + safeFileName)
                .category(IncidentCategory.VALIDATION)
                .severity(IncidentSeverity.WARN)
                .status(IncidentStatus.NEW)
                .errorCode("SGE-WARN-" + safeWarningCode)
                .filename(safeFileName)
                .metadata(buildDocumentWarningMetadata(
                        safeWarningCode,
                        safeWarningMessage,
                        resolvedMethodName,
                        safeFileName,
                        resolvedCallerInfo,
                        resolvedOrigin
                ))
                .createdBy("sistemagestion")
                .timestamp(Instant.now())
                .build();

        try {
            publisher.send(IncidentType.VALIDATION, event);
        } catch (Exception ex) {
            log.warn("No fue posible publicar advertencia documental en RabbitMQ: {}", ex.getMessage());
        }
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        return normalized.length() > 950 ? normalized.substring(0, 950) : normalized;
    }

    private static Environment parseEnvironment(String value) {
        if ("QA".equalsIgnoreCase(value)) {
            return Environment.QA;
        }
        if ("PROD".equalsIgnoreCase(value)) {
            return Environment.PROD;
        }
        return Environment.DEV;
    }

    private CallerMethodResolveraa.CallerInfo resolveWarningCallerInfo(
            String methodName,
            CallerMethodResolveraa.CallerInfo callerInfo
    ) {
        if (callerInfo != null) {
            return callerInfo;
        }
        if (isDynamicWarningMethod(methodName)) {
            return CallerMethodResolveraa.resolveCallerInfo(
                    IncidentPublishingService.class,
                    IncidentPublisherServiceBridge.class
            );
        }
        return null;
    }

    private static boolean isDynamicWarningMethod(String methodName) {
        String safeMethodName = sanitize(methodName);
        return safeMethodName.isEmpty() || "DocumentWarning".equalsIgnoreCase(safeMethodName);
    }

    private String buildErrorMetadata(
            String methodName,
            CallerMethodResolveraa.CallerInfo callerInfo,
            Map<String, Object> additionalMetadata
    ) {
        Map<String, Object> metadata = new LinkedHashMap<String, Object>();
        metadata.put("source", "sistemagestion");
        metadata.put("method", methodName);
        metadata.put("classification", buildClassification(IncidentOrigin.SYSTEM_ERROR_DYNAMIC));

        if (callerInfo != null) {
            Map<String, Object> origin = new LinkedHashMap<String, Object>();
            origin.put("className", callerInfo.className());
            origin.put("simpleClassName", callerInfo.simpleClassName());
            origin.put("methodName", callerInfo.methodName());
            origin.put("lineNumber", callerInfo.lineNumber());
            metadata.put("origin", origin);
        }

        if (additionalMetadata != null && !additionalMetadata.isEmpty()) {
            metadata.put("details", additionalMetadata);
        }

        return toMetadataJson(metadata);
    }

    private String buildDocumentWarningMetadata(
            String warningCode,
            String warningMessage,
            String methodName,
            String fileName,
            CallerMethodResolveraa.CallerInfo callerInfo,
            IncidentOrigin incidentOrigin
    ) {
        Map<String, Object> metadata = new LinkedHashMap<String, Object>();
        metadata.put("source", "sistemagestion");
        metadata.put("warningCode", warningCode);
        metadata.put("fileName", fileName);
        metadata.put("method", sanitize(methodName));

        if (callerInfo != null) {
            Map<String, Object> origin = new LinkedHashMap<String, Object>();
            origin.put("className", callerInfo.className());
            origin.put("simpleClassName", callerInfo.simpleClassName());
            origin.put("methodName", callerInfo.methodName());
            origin.put("lineNumber", callerInfo.lineNumber());
            metadata.put("origin", origin);
        }

        Map<String, Object> warning = new LinkedHashMap<String, Object>();
        warning.put("code", warningCode);
        warning.put("message", warningMessage);
        metadata.put("warning", warning);

        metadata.put("classification", buildClassification(incidentOrigin));
        return toMetadataJson(metadata);
    }

    private Map<String, Object> buildClassification(IncidentOrigin origin) {
        Map<String, Object> classification = new LinkedHashMap<String, Object>();
        classification.put("origin", origin.key());
        classification.put("category", origin.category());
        classification.put("methodResolution", origin.methodResolution());
        return classification;
    }

    private String toMetadataJson(Map<String, Object> metadata) {
        try {
            String json = objectMapper.writeValueAsString(metadata);
            if (json.length() <= MAX_METADATA_LENGTH) {
                return json;
            }

            Map<String, Object> reduced = new LinkedHashMap<String, Object>(metadata);
            if (reduced.remove("details") != null) {
                reduced.put("detailsTruncated", true);
                json = objectMapper.writeValueAsString(reduced);
                if (json.length() <= MAX_METADATA_LENGTH) {
                    return json;
                }
            }

            reduced.remove("origin");
            reduced.put("metadataTruncated", true);
            json = objectMapper.writeValueAsString(reduced);
            if (json.length() <= MAX_METADATA_LENGTH) {
                return json;
            }
        } catch (Exception ex) {
            log.warn("No fue posible serializar metadata de incidente: {}", ex.getMessage());
        }

        return "{\"source\":\"sistemagestion\",\"metadataTruncated\":true}";
    }

    private static String toJson(Map<String, Object> metadata) {
        try {
            return OBJECT_MAPPER.writeValueAsString(metadata);
        } catch (Exception e) {
            return "{\"error\":\"metadata_serialization_failed\"}";
        }
    }

    private String buildMetadataJson(DocumentWarning warning) {
        Map<String, Object> metadata = buildMetadata(warning);
        return toJson(metadata);
    }

    private String resolveFileType(String fileName, String defaultType) {
        String safe = sanitize(fileName);
        int lastDot = safe.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < safe.length() - 1) {
            return sanitize(safe.substring(lastDot + 1)).toLowerCase();
        }
        return defaultType;
    }

}
