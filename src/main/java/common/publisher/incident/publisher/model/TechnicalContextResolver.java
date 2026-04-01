package common.publisher.incident.publisher.model;

public final class TechnicalContextResolver {

    // Clases concretas a excluir (infraestructura)
    private static final String[] EXCLUDED_CLASSES = new String[] {
            TechnicalContextResolver.class.getName(),
            "common.publisher.common.ArchivoTexto",
            "common.publisher.common.IncidentPublisherServiceBridge"
    };

    // Prefijos técnicos (JDK + proxies comunes)
    private static final String[] EXCLUDED_PREFIXES = new String[] {
            "java.",
            "javax.",
            "sun.",
            "com.sun.",
            "jdk."
    };

    private TechnicalContextResolver() {}

    // =========================
    // API PRINCIPAL (TIPADA)
    // =========================
    public static TechnicalContext resolveTyped() {

        StackTraceElement[] stack = new Throwable().getStackTrace();

        // 1. Buscar caller real (primer candidato válido)
        for (StackTraceElement el : stack) {
            if (isCandidate(el)) {
                return buildTypedContext(el, TechnicalContext.ResolutionStrategy.PRIMARY);
            }
        }

        // 2. Fallback
        return buildTypedFallback(stack);
    }

    // =========================
    // CORE LOGIC
    // =========================
    private static boolean isCandidate(StackTraceElement el) {
        String className = el.getClassName();

        // Excluir prefijos técnicos
        if (startsWithAny(className, EXCLUDED_PREFIXES)) {
            return false;
        }

        // Excluir clases específicas (incluyendo proxies)
        return !isExcludedClassOrProxy(className);
    }

    private static boolean isExcludedClassOrProxy(String className) {
        for (String excluded : EXCLUDED_CLASSES) {
            if (className.equals(excluded)
                    || className.startsWith(excluded + "$$")
                    || className.startsWith(excluded + "$")) {
                return true;
            }
        }
        return false;
    }

    private static boolean startsWithAny(String value, String[] prefixes) {
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    // =========================
    // BUILDERS TIPADOS
    // =========================
    private static TechnicalContext buildTypedContext(
            StackTraceElement el,
            TechnicalContext.ResolutionStrategy strategy
    ) {
        return new TechnicalContext(
                el.getClassName(),
                normalizeMethodName(el.getMethodName()),
                el.getLineNumber(),
                Thread.currentThread().getName(),
                strategy
        );
    }

    /**
     * Normaliza el nombre del método generado por la JVM.
     * <p>
     * Las lambdas tienen la forma {@code lambda$metodoOrigen$N} → se extrae {@code metodoOrigen}.
     * Los métodos sintéticos de acceso tienen la forma {@code access$N} → se devuelve "access".
     * El resto se devuelve sin cambios.
     * </p>
     */
    private static String normalizeMethodName(String methodName) {
        if (methodName == null) {
            return "UNKNOWN";
        }
        // lambda$cargarAutoconsumo$0  →  cargarAutoconsumo
        if (methodName.startsWith("lambda$")) {
            String withoutPrefix = methodName.substring("lambda$".length());
            int lastDollar = withoutPrefix.lastIndexOf('$');
            return lastDollar >= 0 ? withoutPrefix.substring(0, lastDollar) : withoutPrefix;
        }
        // access$000, access$100, ...  →  access
        if (methodName.startsWith("access$")) {
            return "access";
        }
        return methodName;
    }

    private static TechnicalContext buildTypedFallback(StackTraceElement[] stack) {

        StackTraceElement lastValid = null;

        for (StackTraceElement el : stack) {
            if (isCandidate(el)) {
                lastValid = el;
            }
        }

        if (lastValid != null) {
            return buildTypedContext(
                    lastValid,
                    TechnicalContext.ResolutionStrategy.FALLBACK
            );
        }

        return null;
    }
}