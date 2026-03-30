package common.publisher.incident.publisher;

import app.config.ApplicationContextUtils;
import java.util.Map;

import common.publisher.incident.publisher.model.DocumentWarning;
import org.springframework.context.ApplicationContext;

/**
 * Puente estatico para publicar incidentes desde codigo legacy no gestionado por Spring
 * (por ejemplo utilidades estaticas).
 *
 * <p>Si estas en un bean Spring (@Service, @Controller, etc.) usa directamente
 * {@link IncidentPublishingService} por inyeccion de dependencias.</p>
 */
public final class IncidentPublisherServiceBridge {

    private static final String INCIDENT_PUBLISHING_SERVICE_BEAN = "incidentPublishingService";
    private IncidentPublisherServiceBridge() {
    }

    /**
     * Publica un incidente de tipo SYSTEM usando el servicio de aplicacion.
     *
     * @param message contenido del error
     * @param methodName origen funcional del error
     * @param additionalMetadata metadata adicional para diagnostico
     */
    public static void publishSystemError(String message, String methodName, Map<String, Object> additionalMetadata) {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();
        if (context == null) {
            return;
        }

        IncidentPublishingService service = context.getBean(
                INCIDENT_PUBLISHING_SERVICE_BEAN,
                IncidentPublishingService.class
        );
        service.publishSystemError(message, methodName, additionalMetadata);
    }


    public static void publishDocumentWarning(DocumentWarning warning) {

        ApplicationContext context = ApplicationContextUtils.getApplicationContext();

        if (context == null || warning == null) {
            return;
        }

        IncidentPublishingService service = context.getBean(
                INCIDENT_PUBLISHING_SERVICE_BEAN,
                IncidentPublishingService.class
        );

        service.publishDocumentWarning(warning);
    }

}


