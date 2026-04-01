package common.publisher.incident.publisher;

import app.incidents.publisher.PublisherException;
import common.publisher.common.Publisher;
import common.publisher.incident.config.IncidentPublisherProperties;
import common.publisher.incident.contract.IncidentEvent;
import common.publisher.incident.contract.IncidentPublisherMessageKey;
import common.i18n.Messages;
import common.publisher.incident.contract.IncidentType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RequiredArgsConstructor
public class IncidentPublisher implements Publisher {

    private final RabbitTemplate rabbitTemplate;
    private final IncidentPublisherProperties properties;

    @Override
    public void send(IncidentType type, IncidentEvent event) {
        IncidentPublisherProperties.PublishTarget target = properties.forType(type);
        if (target == null) {
            throw new PublisherException(Messages.format(
                    IncidentPublisherMessageKey.INCIDENT_TYPE_NOT_CONFIGURED,
                    type.key()
            ));
        }

        rabbitTemplate.convertAndSend(target.getExchange(), target.getRoutingKey(), event);
    }

}
