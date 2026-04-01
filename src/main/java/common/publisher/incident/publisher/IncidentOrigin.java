package common.publisher.incident.publisher;

public enum IncidentOrigin {

    SYSTEM_ERROR_DYNAMIC("system.error.dynamic", "error", "dynamic"),
    DOCUMENT_WARNING_STATIC("document.warning.static", "warning", "static"),
    DOCUMENT_WARNING_DYNAMIC("document.warning.dynamic", "warning", "dynamic"),;

    private final String key;
    private final String category;
    private final String methodResolution;

    IncidentOrigin(String key, String category, String methodResolution) {
        this.key = key;
        this.category = category;
        this.methodResolution = methodResolution;
    }

    public String key() {
        return key;
    }

    public String category() {
        return category;
    }

    public String methodResolution() {
        return methodResolution;
    }

}
