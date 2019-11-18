package cz.wake.corgibot.utils.lang;

import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.config.Config;
import cz.wake.corgibot.utils.config.ConfigLoader;

import java.io.File;

public class LanguageObject {

    private final Language language;
    private Config config;

    LanguageObject(Language lang) {
        this.language = lang;
        try {
            config = ConfigLoader.getConfig(new File("langs/lang_" + language.getCode() + ".json"));
        } catch (Exception e) {
            CorgiLogger.fatalMessage("Error with loading language: " + language.getEnglishName());
            e.printStackTrace();
        }
    }

    public Language getLanguage() {
        return language;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return language.getNativeName();
    }


}
