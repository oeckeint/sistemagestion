package common.publisher.incident.publisher;

import common.i18n.Messages;
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
import java.util.Map;
import java.util.UUID;

import common.publisher.incident.publisher.model.DocumentWarning;
import common.publisher.incident.publisher.model.TechnicalContext;
import controladores.helper.Utilidades;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ObjectProvider<Publisher> publisherProvider;

    @Value("${app.incident-publisher.service-name:sistemagestion}")
    private String serviceName;

    @Value("${app.incident-publisher.environment}")
    private String environment;

    @Value("${app.incident-publisher.default-type:system}")
    private String defaultType;

    public IncidentPublishingService(@Qualifier("incidentPublisher") ObjectProvider<Publisher> publisherProvider) {
        this.publisherProvider = publisherProvider;
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

                // origen técnico
                .methodName(methodName)
                .endpoint(sanitize(warning.getEndpoint()))
                .httpMethod(String.valueOf(warning.getHttpMethod()))
                .userId(resolveCurrentUser())

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
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("code", sanitize(warning.resolvedCode()));
        metadata.put("flow", sanitize(String.valueOf(warning.getFlow())));

        // data de negocio
        if (warning.getData() != null) {
            metadata.put("data", warning.getData());
        }

        // contexto técnico
        TechnicalContext ctx = warning.getTechnicalContext();

        if (ctx != null) {
            Map<String, Object> tech = new HashMap<>();
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

        return sanitize(base);
    }

    private static String resolveCurrentUser() {
        try {
            String user = Utilidades.currentUser();
            return (user != null && !user.isEmpty()) ? user : "SYSTEM";
        } catch (Exception e) {
            return "SYSTEM";
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

    private String buildMetadataJson(DocumentWarning warning) {
        Map<String, Object> metadata = buildMetadata(warning);
        return toJson(metadata);
    }

    private static String toJson(Map<String, Object> metadata) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(metadata);
            // Validar y truncar si excede el límite
            if (json.length() > MAX_METADATA_LENGTH) {
                log.warn("Metadata JSON excede {} caracteres ({} actual). Se truncará.", 
                    MAX_METADATA_LENGTH, json.length());
                return json.substring(0, MAX_METADATA_LENGTH - 4) + "...}";
            }
            return json;
        } catch (Exception e) {
            return "{\"error\":\"metadata_serialization_failed\"}";
        }
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
