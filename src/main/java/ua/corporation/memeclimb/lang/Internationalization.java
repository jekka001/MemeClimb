package ua.corporation.memeclimb.lang;

import lombok.Setter;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Setter
public class Internationalization {
    private static final ResourceBundleMessageSource MESSAGE_SOURCE = new ResourceBundleMessageSource();
    private Lang lang = Lang.EN;

    public Internationalization() {
        MESSAGE_SOURCE.setBasenames("lang/messages");
        MESSAGE_SOURCE.setDefaultEncoding("UTF-8");
    }

    public String getLocalizationMessage(String key) {
        return MESSAGE_SOURCE.getMessage(key, null, Locale.forLanguageTag(lang.getLang()));
    }

}
