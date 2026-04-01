package common.publisher.incident.publisher;

import app.config.ApplicationContextUtils;
import java.util.Map;

import common.i18n.Messages;
import common.i18n.SGECommonMessageKey;
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

    private IncidentPublisherServiceBridge() {
        throw new IllegalStateException(Messages.get(SGECommonMessageKey.UTILITY_CLASS));
    }

    public static void publishDocumentWarning(DocumentWarning warning) {
        ApplicationContext context = ApplicationContextUtils.getApplicationContext();

        if (context == null || warning == null) return;

        IncidentPublishingService service = context.getBean(IncidentPublishingService.class);
        service.publishDocumentWarning(warning);
    }

}
