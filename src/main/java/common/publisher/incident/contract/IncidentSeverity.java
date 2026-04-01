package common.publisher.incident.contract;

public enum IncidentSeverity {

    CRITICAL(4),
    ERROR(3),
    WARN(2),
    INFO(1);

    private final int level;

    IncidentSeverity(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

}
