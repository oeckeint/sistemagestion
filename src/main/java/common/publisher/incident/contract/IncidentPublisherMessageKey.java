package common.publisher.incident.contract;

import common.i18n.MessageKey;

public enum IncidentPublisherMessageKey implements MessageKey {

    INCIDENT_TYPE_NOT_CONFIGURED("incident.publisher.type.not.configured");

    private static final String BUNDLE = "i18n.messages_incidents";

    private final String key;

    IncidentPublisherMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public String bundle() {
        return BUNDLE;
    }

}
