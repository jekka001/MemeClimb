package ua.corporation.memeclimb.lang;

import lombok.Getter;

@Getter
public enum Lang {
    EN("en"),
    UA("ua");

    private final String lang;

    Lang(String lang) {
        this.lang = lang;
    }
}
