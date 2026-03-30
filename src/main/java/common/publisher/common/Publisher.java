package common.publisher.common;

import common.publisher.incident.contract.IncidentEvent;
import common.publisher.incident.contract.IncidentType;

public interface Publisher {

    void send(IncidentType type, IncidentEvent event);

}
