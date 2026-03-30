package common.publisher.incident.publisher.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Utilidad para obtener dinamicamente el metodo invocador desde el stacktrace.
 */
public final class CallerMethodResolveraa {

    private static final String UNKNOWN_CALLER = "UnknownCaller";

    private CallerMethodResolveraa() {
    }

    /**
     * Captura el caller actual sin necesidad de indicar clases a omitir.
     */
    public static String captureCallerMethod() {
        return resolveCallerMethod();
    }

    /**
     * Captura la informacion estructurada del caller actual sin indicar clases a omitir.
     */
    public static CallerInfo captureCallerInfo() {
        return resolveCallerInfo();
    }

    public static String resolveCallerMethod(Class<?>... classesToSkip) {
        return resolveCallerInfo(classesToSkip).displayName();
    }

    public static CallerInfo resolveCallerInfo(Class<?>... classesToSkip) {
        Set<String> skipClassNames = new HashSet<String>();
        skipClassNames.add(Thread.class.getName());
        skipClassNames.add(CallerMethodResolveraa.class.getName());

        if (classesToSkip != null) {
            for (Class<?> clazz : classesToSkip) {
                if (clazz != null) {
                    skipClassNames.add(clazz.getName());
                }
            }
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            if (skipClassNames.contains(className)
                    || "getStackTrace".equals(methodName)
                    || "resolveCallerInfo".equals(methodName)
                    || "resolveCallerMethod".equals(methodName)) {
                continue;
            }

            int lastDot = className.lastIndexOf('.');
            String simpleClassName = lastDot >= 0 ? className.substring(lastDot + 1) : className;
            return new CallerInfo(className, simpleClassName, methodName, element.getLineNumber());
        }

        return new CallerInfo(UNKNOWN_CALLER, UNKNOWN_CALLER, UNKNOWN_CALLER, -1);
    }

    public static final class CallerInfo {
        private final String className;
        private final String simpleClassName;
        private final String methodName;
        private final int lineNumber;

        public CallerInfo(String className, String simpleClassName, String methodName, int lineNumber) {
            this.className = className;
            this.simpleClassName = simpleClassName;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
        }

        public String className() {
            return className;
        }

        public String simpleClassName() {
            return simpleClassName;
        }

        public String methodName() {
            return methodName;
        }

        public int lineNumber() {
            return lineNumber;
        }

        public String displayName() {
            return simpleClassName + "." + methodName;
        }
    }

}
