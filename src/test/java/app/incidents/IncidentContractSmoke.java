package app.incidents;

import common.publisher.common.Environment;
import common.publisher.incident.contract.IncidentCategory;
import common.publisher.incident.contract.IncidentEvent;
import common.publisher.incident.contract.IncidentSeverity;
import common.publisher.incident.contract.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public class IncidentContractSmoke {

    public static void main(String[] args) {
        IncidentEvent event = IncidentEvent.builder()
                .id(UUID.randomUUID().toString())
                .serviceName("sistemagestion")
                .environment(Environment.DEV)
                .exceptionType("RuntimeException")
                .message("Smoke")
                .category(IncidentCategory.SYSTEM)
                .severity(IncidentSeverity.ERROR)
                .status(IncidentStatus.NEW)
                .timestamp(Instant.now())
                .build();

        if (!"sistemagestion".equals(event.getServiceName())) {
            throw new IllegalStateException("serviceName invalido");
        }
        if (event.getTimestamp() == null) {
            throw new IllegalStateException("timestamp no generado");
        }

        System.out.println("IncidentContractSmoke OK");
    }
}

