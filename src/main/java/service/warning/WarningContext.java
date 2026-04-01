package service.warning;

import common.publisher.incident.publisher.model.*;
import controladores.common.HTTPMethod;
import lombok.Getter;

import java.util.EnumMap;
import java.util.function.Consumer;

@Getter
public class WarningContext {

    private final String fileName;
    private final FileType fileType;
    private final HTTPMethod httpMethod;
    private final String endpoint;
    private final Flow flow;
    private final EnumMap<DataKeys, String> data;
    private final TechnicalContext technicalContext;
    private final Consumer<String> errorConsumer;

    private WarningContext(Builder builder) {
        this.fileName = builder.fileName;
        this.fileType = builder.fileType;
        this.endpoint = builder.endpoint;
        this.httpMethod = builder.httpMethod;
        this.flow = builder.flow;
        this.data = new EnumMap<>(builder.data);
        this.technicalContext = builder.technicalContext;
        this.errorConsumer = builder.errorConsumer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fileName;
        private FileType fileType;
        private String endpoint;
        private HTTPMethod httpMethod;
        private Flow flow;
        private final EnumMap<DataKeys, String> data = new EnumMap<>(DataKeys.class);
        private TechnicalContext technicalContext;
        private Consumer<String> errorConsumer = code -> {}; // default no-op

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder fileType(FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder httpMethod(HTTPMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder flow(Flow flow) {
            this.flow = flow;
            return this;
        }

        public Builder put(DataKeys key, String value) {
            this.data.put(key, value);
            return this;
        }

        public Builder technicalContext(TechnicalContext technicalContext) {
            this.technicalContext = technicalContext;
            return this;
        }

        public Builder errorConsumer(Consumer<String> consumer) {
            this.errorConsumer = consumer;
            return this;
        }

        public WarningContext build() {
            if (fileName == null) {
                throw new IllegalStateException("fileName is required");
            }
            if (fileType == null) {
                throw new IllegalStateException("fileType is required");
            }

            if (endpoint == null) {
                throw new IllegalStateException("endpoint is required");
            }

            if (flow == null) {
                throw new IllegalStateException("flow is required");
            }

            if (this.technicalContext == null) {
                this.technicalContext = TechnicalContextResolver.resolveTyped();
            }

            return new WarningContext(this);
        }

    }

}
