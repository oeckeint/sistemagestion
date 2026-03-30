package controladores.common;

import common.i18n.Messages;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import javax.validation.constraints.NotNull;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@NoArgsConstructor
public class ControladoresMessagesLogger {

    public String format(@NotNull ControladoresMessageKey key, Object... args) {
        return Messages.format(key, args);
    }

    private void logIfEnabled(
            Logger logger,
            BooleanSupplier isEnabled,
            BiConsumer<Logger, String> logMethod,
            Supplier<String> messageSupplier
    ) {
        if (isEnabled.getAsBoolean()) {
            logMethod.accept(logger, messageSupplier.get());
        }
    }

    private void logIfEnabledWithThrowable(
            Logger logger,
            BooleanSupplier isEnabled,
            TriConsumer<Logger, String, Throwable> logMethod,
            Supplier<String> messageSupplier,
            Throwable throwable
    ) {
        if (isEnabled.getAsBoolean()) {
            logMethod.accept(logger, messageSupplier.get(), throwable);
        }
    }

    public void debug(@NotNull Logger logger, @NotNull ControladoresMessageKey key, Object... args) {
        logIfEnabled(logger, logger::isDebugEnabled, Logger::debug, () -> format(key, args));
    }

    public void info(@NotNull Logger logger, @NotNull ControladoresMessageKey key, Object... args) {
        logIfEnabled(logger, logger::isInfoEnabled, Logger::info, () -> format(key, args));
    }

    public void warn(@NotNull Logger logger, @NotNull ControladoresMessageKey key, Object... args) {
        logIfEnabled(logger, logger::isWarnEnabled, Logger::warn, () -> format(key, args));
    }

    public void warn(@NotNull Logger logger, @NotNull String message) {
        logIfEnabled(logger, logger::isWarnEnabled, Logger::warn, () -> message);
    }

    public void error(@NotNull Logger logger, @NotNull ControladoresMessageKey key, Object... args) {
        logIfEnabled(logger, logger::isErrorEnabled, Logger::error, () -> format(key, args));
    }

    public void error(
            @NotNull Logger logger,
            @NotNull ControladoresMessageKey key,
            @NotNull Throwable throwable,
            Object... args
    ) {
        logIfEnabledWithThrowable(
                logger,
                logger::isErrorEnabled,
                Logger::error,
                () -> format(key, args),
                throwable
        );
    }

    public void trace(@NotNull Logger logger, @NotNull ControladoresMessageKey key, Object... args) {
        logIfEnabled(logger, logger::isTraceEnabled, Logger::trace, () -> format(key, args));
    }

    /**
     * Functional interface porque Java no tiene TriConsumer nativo
     */
    @FunctionalInterface
    private interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

}
