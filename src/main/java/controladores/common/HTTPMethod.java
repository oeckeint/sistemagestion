package controladores.common;

public enum HTTPMethod {

    GET,
    POST,
    PUT,
    DELETE,
    PATCH;

    // 🔹 Clasificación semántica
    public boolean isRead() {
        return this == GET;
    }

    public boolean isWrite() {
        return this == POST || this == PUT || this == PATCH || this == DELETE;
    }

    public boolean isIdempotent() {
        return this == GET || this == PUT || this == DELETE;
    }

    public boolean allowsBody() {
        return this == POST || this == PUT || this == PATCH;
    }

    // 🔹 Formato controlado (evita transformaciones externas)
    public String value() {
        return name(); // estándar HTTP (uppercase)
    }

    public String lower() {
        return name().toLowerCase();
    }

    // 🔹 Para logging estructurado (clave consistente)
    public String logKey() {
        return "http.method";
    }

}
