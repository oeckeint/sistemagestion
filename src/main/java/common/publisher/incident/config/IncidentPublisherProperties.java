package common.publisher.incident.config;


import common.publisher.incident.contract.IncidentType;

import java.util.HashMap;
import java.util.Map;

public class IncidentPublisherProperties {

    private boolean enabled = true;
    private Map<String, PublishTarget> types = new HashMap<String, PublishTarget>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, PublishTarget> getTypes() {
        return types;
    }

    public void setTypes(Map<String, PublishTarget> types) {
        this.types = types;
    }

    public PublishTarget forType(IncidentType type) {
        return types.get(type.key());
    }

    public static class PublishTarget {
        private final String exchange;
        private final String routingKey;
        private final String queue;
        private final String deadLetterExchange;
        private final String deadLetterQueue;

        public PublishTarget(String exchange, String routingKey, String queue, String deadLetterExchange, String deadLetterQueue) {
            this.exchange = exchange;
            this.routingKey = routingKey;
            this.queue = queue;
            this.deadLetterExchange = deadLetterExchange;
            this.deadLetterQueue = deadLetterQueue;
        }

        public String getExchange() {
            return exchange;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public String getQueue() {
            return queue;
        }

        public String getDeadLetterExchange() {
            return deadLetterExchange;
        }

        public String getDeadLetterQueue() {
            return deadLetterQueue;
        }
    }
}

