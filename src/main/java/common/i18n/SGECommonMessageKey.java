package common.i18n;

public enum SGECommonMessageKey implements MessageKey{
    UTILITY_CLASS("utility.class.message"),
    ;

    private final String key;

    SGECommonMessageKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
