package ua.corporation.memeclimb.lang;

public enum Text {
    EMPTY_BALANCE("emptyBalance"),
    FIRST_PROCESSING("firstProcessingMessage"),
    SECOND_PROCESSING("secondProcessingMessage"),
    THIRD_PROCESSING("thirdProcessingMessage");

    private final String key;

    Text(String key) {
        this.key = key;
    }

    public String getKey(Internationalization internationalization) {
        return internationalization.getLocalizationMessage(key);
    }
}
