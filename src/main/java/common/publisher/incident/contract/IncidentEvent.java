package common.publisher.incident.contract;

import common.publisher.common.Environment;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
public class IncidentEvent {

    private static final int MAX_METADATA_LENGTH = 1000;

    private final String id;
    private final String serviceName;
    private final Environment environment;
    private final String endpoint;
    private final String methodName;
    private final String httpMethod;
    private final String traceId;
    private final String spanId;
    private final String userId;
    private final String exceptionType;
    private final String message;
    private final String stackTrace;
    private final IncidentCategory category;
    private final IncidentSeverity severity;
    private final IncidentStatus status;
    private final String errorCode;
    private final String filename;
    private final String fileType;
    private final String folderName;
    private final String metadata;
    private final String createdBy;
    private final LocalDateTime updatedOn;
    private final String updatedBy;
    private final Instant timestamp;

    private IncidentEvent(Builder builder) {
        this.id = requireText(builder.id, "id is required");
        this.serviceName = requireText(builder.serviceName, "serviceName is required");

        this.environment = builder.environment;
        this.endpoint = builder.endpoint;
        this.methodName = builder.methodName;
        this.httpMethod = builder.httpMethod;
        this.traceId = builder.traceId;
        this.spanId = builder.spanId;
        this.userId = builder.userId;

        this.exceptionType = requireText(builder.exceptionType, "exceptionType is required");
        this.message = builder.message;
        this.stackTrace = builder.stackTrace;

        this.category = requireNonNull(builder.category, "category is required");
        this.severity = requireNonNull(builder.severity, "severity is required");
        this.status = builder.status;

        this.errorCode = builder.errorCode;

        this.filename = builder.filename;
        this.fileType = builder.fileType;
        this.folderName = builder.folderName;

        this.metadata = validateMetadata(builder.metadata);

        this.createdBy = builder.createdBy;
        this.updatedOn = builder.updatedOn;
        this.updatedBy = builder.updatedBy;

        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();

        validateCoherence();
    }

    public static Builder builder() {
        return new Builder();
    }

    // =========================
    // VALIDATIONS
    // =========================

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static String validateMetadata(String metadata) {
        if (metadata != null && metadata.length() > MAX_METADATA_LENGTH) {
            throw new IllegalArgumentException("metadata max length is " + MAX_METADATA_LENGTH);
        }
        return metadata;
    }

    private void validateCoherence() {
        if (filename != null && fileType == null) {
            throw new IllegalArgumentException("fileType is required when filename is present");
        }
    }

    // =========================
    // BUILDER
    // =========================

    public static final class Builder {

        private String id;
        private String serviceName;
        private Environment environment;
        private String endpoint;
        private String methodName;
        private String httpMethod;
        private String traceId;
        private String spanId;
        private String userId;
        private String exceptionType;
        private String message;
        private String stackTrace;
        private IncidentCategory category;
        private IncidentSeverity severity;
        private IncidentStatus status;
        private String errorCode;
        private String filename;
        private String fileType;
        private String folderName;
        private String metadata;
        private String createdBy;
        private LocalDateTime updatedOn;
        private String updatedBy;
        private Instant timestamp;

        private Builder() {}

        public Builder id(String id) { this.id = id; return this; }
        public Builder serviceName(String serviceName) { this.serviceName = serviceName; return this; }
        public Builder environment(Environment environment) { this.environment = environment; return this; }
        public Builder endpoint(String endpoint) { this.endpoint = endpoint; return this; }
        public Builder methodName(String methodName) { this.methodName = methodName; return this; }
        public Builder httpMethod(String httpMethod) { this.httpMethod = httpMethod; return this; }
        public Builder traceId(String traceId) { this.traceId = traceId; return this; }
        public Builder spanId(String spanId) { this.spanId = spanId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder exceptionType(String exceptionType) { this.exceptionType = exceptionType; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder stackTrace(String stackTrace) { this.stackTrace = stackTrace; return this; }
        public Builder category(IncidentCategory category) { this.category = category; return this; }
        public Builder severity(IncidentSeverity severity) { this.severity = severity; return this; }
        public Builder status(IncidentStatus status) { this.status = status; return this; }
        public Builder errorCode(String errorCode) { this.errorCode = errorCode; return this; }
        public Builder filename(String filename) { this.filename = filename; return this; }
        public Builder fileType(String fileType) { this.fileType = fileType; return this; }
        public Builder folderName(String folderName) { this.folderName = folderName; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder updatedOn(LocalDateTime updatedOn) { this.updatedOn = updatedOn; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }

        public IncidentEvent build() {
            return new IncidentEvent(this);
        }
    }
}