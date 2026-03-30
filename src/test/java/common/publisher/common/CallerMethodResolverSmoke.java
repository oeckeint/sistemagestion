package common.publisher.common;

import common.publisher.incident.publisher.model.CallerMethodResolveraa;

public class CallerMethodResolverSmoke {

    public static void main(String[] args) {
        String directCaller = captureDirectCaller();
        if (!"CallerMethodResolverSmoke.captureDirectCaller".equals(directCaller)) {
            throw new IllegalStateException("Caller inesperado (directo): " + directCaller);
        }

        String capturedCaller = captureCurrentCaller();
        if (!"CallerMethodResolverSmoke.captureCurrentCaller".equals(capturedCaller)) {
            throw new IllegalStateException("Caller inesperado (capture): " + capturedCaller);
        }

        CallerMethodResolveraa.CallerInfo directCallerInfo = captureDirectCallerInfo();
        if (!"CallerMethodResolverSmoke".equals(directCallerInfo.simpleClassName())) {
            throw new IllegalStateException("SimpleClassName inesperado: " + directCallerInfo.simpleClassName());
        }
        if (!"captureDirectCallerInfo".equals(directCallerInfo.methodName())) {
            throw new IllegalStateException("MethodName inesperado: " + directCallerInfo.methodName());
        }
        if (directCallerInfo.lineNumber() <= 0) {
            throw new IllegalStateException("LineNumber invalido: " + directCallerInfo.lineNumber());
        }

        CallerMethodResolveraa.CallerInfo capturedCallerInfo = captureCurrentCallerInfo();
        if (!"CallerMethodResolverSmoke.captureCurrentCallerInfo".equals(capturedCallerInfo.displayName())) {
            throw new IllegalStateException("CallerInfo inesperado (capture): " + capturedCallerInfo.displayName());
        }

        String externalCaller = new ExternalCaller().captureWithSkip();
        if (!"CallerMethodResolverSmoke$ExternalCaller.captureWithSkip".equals(externalCaller)) {
            throw new IllegalStateException("Caller inesperado (con skip): " + externalCaller);
        }

        System.out.println("CallerMethodResolverSmoke OK");
    }

    private static String captureDirectCaller() {
        return CallerMethodResolveraa.resolveCallerMethod();
    }

    private static CallerMethodResolveraa.CallerInfo captureDirectCallerInfo() {
        return CallerMethodResolveraa.resolveCallerInfo();
    }

    private static String captureCurrentCaller() {
        return CallerMethodResolveraa.captureCallerMethod();
    }

    private static CallerMethodResolveraa.CallerInfo captureCurrentCallerInfo() {
        return CallerMethodResolveraa.captureCallerInfo();
    }

    private static final class ExternalCaller {
        private String captureWithSkip() {
            return CallerMethodResolveraa.resolveCallerMethod(CallerMethodResolverSmoke.class);
        }
    }
}



