package common.i18n;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Messages {

    private static final Map<String, ResourceBundle> BUNDLES = new ConcurrentHashMap<String, ResourceBundle>();

    protected Messages() {
    }

    public static String get(MessageKey key) {
        try {
            ResourceBundle bundle = resolveBundle(key.bundle());
            return bundle.getString(key.key());
        } catch (MissingResourceException ex) {
            return key.key();
        }
    }

    public static String format(MessageKey key, Object... args) {
        return MessageFormat.format(get(key), args);
    }

    private static ResourceBundle resolveBundle(String bundleName) {
        return BUNDLES.computeIfAbsent(bundleName, ResourceBundle::getBundle);
    }

}
