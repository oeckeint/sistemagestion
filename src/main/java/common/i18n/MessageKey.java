package common.i18n;

public interface MessageKey {

    String DEFAULT_BUNDLE = "i18n.messages_common";

    String key();

    default String bundle() {
        return DEFAULT_BUNDLE;
    }

}



