package common.publisher.incident.publisher.model;

import controladores.common.WarningType;
import controladores.common.structure.ArchivoTextoMessageKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class DocumentWarning {

    private final WarningType warning;
    private final String messageOverride;
    private final String fileName;
    private final FileType fileType;
    private final Flow flow;

    private final Map<DataKeys, Object> data;
    private final TechnicalContext technicalContext;


    public Map<String, Object> getSerializedData() {
        if (data == null) return null;

        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().key(),
                        Map.Entry::getValue
                ));
    }

    public DocumentWarning withTechnicalContext(TechnicalContext context) {
        return this.toBuilder()
                .technicalContext(context)
                .build();
    }

    public String resolvedCode() {
        return warning.getCode();
    }

    public ArchivoTextoMessageKey resolvedMessageKey() {
        return warning.getMessageKey();
    }

}
