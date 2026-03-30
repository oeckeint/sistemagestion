package common.publisher.incident.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import common.publisher.common.Publisher;
import common.publisher.incident.contract.IncidentEvent;
import common.publisher.incident.contract.IncidentType;
import common.publisher.incident.publisher.IncidentPublisher;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class IncidentPublisherConfig {

    @Bean
    public ConnectionFactory incidentRabbitConnectionFactory(Environment env) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(env.getProperty("spring.rabbitmq.host", "localhost"));
        connectionFactory.setPort(Integer.parseInt(env.getProperty("spring.rabbitmq.port", "5672")));
        connectionFactory.setUsername(env.getProperty("spring.rabbitmq.username", "guest"));
        connectionFactory.setPassword(env.getProperty("spring.rabbitmq.password", "guest"));
        connectionFactory.setVirtualHost(env.getProperty("spring.rabbitmq.virtual-host", "/"));
        return connectionFactory;
    }

    @Bean
    public ObjectMapper incidentObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Jackson2JsonMessageConverter incidentJsonMessageConverter(ObjectMapper incidentObjectMapper) {
        return new Jackson2JsonMessageConverter(incidentObjectMapper);
    }

    @Bean
    public RabbitTemplate incidentRabbitTemplate(
            ConnectionFactory incidentRabbitConnectionFactory,
            Jackson2JsonMessageConverter incidentJsonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(incidentRabbitConnectionFactory);
        template.setMessageConverter(incidentJsonMessageConverter);
        return template;
    }

    @Bean
    public IncidentPublisherProperties incidentPublisherProperties(Environment env) {
        IncidentPublisherProperties properties = new IncidentPublisherProperties();
        properties.setEnabled(Boolean.parseBoolean(env.getProperty("c4e.incidents.enabled", "true")));

        Map<String, IncidentPublisherProperties.PublishTarget> targets =
                new HashMap<String, IncidentPublisherProperties.PublishTarget>();

        for (IncidentType type : IncidentType.values()) {
            String prefix = "c4e.incidents.types." + type.key() + ".";
            String exchange = env.getProperty(prefix + "exchange");
            String routingKey = firstNonBlank(
                    env.getProperty(prefix + "routing-key"),
                    env.getProperty(prefix + "routingKey")
            );

            if (hasText(exchange) && hasText(routingKey)) {
                targets.put(type.key(), new IncidentPublisherProperties.PublishTarget(
                        exchange,
                        routingKey,
                        env.getProperty(prefix + "queue"),
                        firstNonBlank(
                                env.getProperty(prefix + "dead-letter-exchange"),
                                env.getProperty(prefix + "deadLetterExchange")
                        ),
                        firstNonBlank(
                                env.getProperty(prefix + "dead-letter-queue"),
                                env.getProperty(prefix + "deadLetterQueue")
                        )
                ));
            }
        }

        properties.setTypes(targets);
        return properties;
    }

    @Bean(name = "incidentPublisher")
    public Publisher incidentPublisher(RabbitTemplate incidentRabbitTemplate, IncidentPublisherProperties properties) {
        if (!properties.isEnabled()) {
            return new Publisher() {
                @Override
                public void send(IncidentType type, IncidentEvent event) {
                    // Publication intentionally disabled.
                }
            };
        }

        return new IncidentPublisher(incidentRabbitTemplate, properties);
    }

    private static String firstNonBlank(String first, String second) {
        if (hasText(first)) {
            return first;
        }
        return hasText(second) ? second : null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
