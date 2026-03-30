package common.publisher.incident.publisher.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TechnicalContext {

    public enum ResolutionStrategy {
        PRIMARY,   // caller directo
        FALLBACK   // degradado
    }

    private final String className;
    private final String method;
    private final int line;
    private final String thread;
    private final ResolutionStrategy resolutionStrategy;

}
