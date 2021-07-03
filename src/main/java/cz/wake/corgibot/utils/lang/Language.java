package cz.wake.corgibot.utils.lang;

import cz.wake.corgibot.utils.EmoteList;

import java.util.ArrayList;
import java.util.Collection;

public enum Language {

    EN_US("en", "US", "English", "English", EmoteList.ENGLISH_FLAG),
    CZ_CS("cz", "CZ", "Čeština", "Czech", EmoteList.CZECH_FLAG),
    SK_SK("sk", "SK", "Slovenština", "Slovak", EmoteList.SLOVAK_FLAG);

    private final String code;
    private final String nativeName;
    private final String englishName;
    private final String flag;

    Language(String language, String country, String nativeName, String englishName, String flag) {
        this.code = language;
        this.nativeName = nativeName;
        this.englishName = englishName;
        this.flag = flag;
    }

    public static Language parse(String string) {
        for (Language language : values()) {
            if (language.getEnglishName().equalsIgnoreCase(string)
                    || language.getNativeName().equalsIgnoreCase(string)
                    || language.getCode().equalsIgnoreCase(string)) {
                return language;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getFlag() {
        return flag;
    }

    private static class LanguageList extends ArrayList<String> {

        LanguageList(Collection<? extends String> c) {
            super(c);
        }

        @Override
        public boolean contains(Object o) {
            String argument = (String) o;
            for (String item : this) {
                if (argument.equalsIgnoreCase(item)) {
                    return true;
                }
            }
            return false;
        }
    }
}
