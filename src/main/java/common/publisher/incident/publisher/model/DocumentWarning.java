package common.publisher.incident.publisher.model;

import controladores.common.HTTPMethod;
import controladores.common.WarningType;
import controladores.common.structure.ArchivoTextoMessageKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class DocumentWarning {

    private final WarningType warning;
    private final String messageOverride;
    private final String endpoint;
    private final HTTPMethod httpMethod;
    private final String fileName;
    private final FileType fileType;
    private final Flow flow;

    private final Map<DataKeys, String> data;
    private final TechnicalContext technicalContext;


    public Map<String, Object> getSerializedData() {
        if (data == null) return Collections.emptyMap();

        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().key(),
                        Map.Entry::getValue
                ));
    }

    public String resolvedCode() {
        return warning.getCode();
    }

    public ArchivoTextoMessageKey resolvedMessageKey() {
        return warning.getMessageKey();
    }

}
