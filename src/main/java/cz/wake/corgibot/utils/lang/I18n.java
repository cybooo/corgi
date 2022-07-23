package cz.wake.corgibot.utils.lang;

import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.config.Config;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class I18n {

    public static final LanguageObject DEFAULT = new LanguageObject(Language.EN_US);
    public static final Set<LanguageObject> LANGS = new HashSet<>();

    public static void start() {

        LANGS.add(DEFAULT);
        Language[] values = Language.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            Language language = values[i];
            if (DEFAULT.getLanguage().equals(language)) {
                continue;
            }
            LANGS.add(new LanguageObject(language));
        }
        CorgiLogger.infoMessage("Loaded (" + LANGS.size() + ") languages: " + LANGS);
    }

    public static String getString(@Nonnull Guild guild, String string, Object... args) {
        String message = getString(guild, string);
        if (message == null) {
            return null;
        }
        return format(message, args);
    }

    @Nullable
    public static String getString(@Nullable Guild guild, String string) {
        if (string == null) {
            return null;
        }
        return get(guild).getString(string, DEFAULT.getConfig().getString(string, null));
    }

    @Nonnull
    public static Config get(@Nullable Guild guild) {
        if (guild == null) {
            return DEFAULT.getConfig();
        }
        return getLocale(guild).getConfig();
    }

    @Nonnull
    public static String getLoc(@Nonnull GuildWrapper guild, String route) {
        return getLocale(guild).getConfig().getString(route);
    }

    @Nonnull
    public static LanguageObject getLocale(@Nonnull GuildWrapper guild) {
        try {
            for (LanguageObject locale : LANGS) {
                if (locale.getLanguage().getCode().equalsIgnoreCase(guild.getLanguage())) {
                    return locale;
                }
            }
        } catch (Exception e) {
            CorgiLogger.dangerMessage("Error when reading entity:\n");
            e.printStackTrace();
        }
        return DEFAULT;
    }

    @Nonnull
    public static LanguageObject getLocale(@Nonnull Guild guild) {
        try {
            GuildWrapper wrapper = BotManager.getCustomGuild(guild.getId());

            if (wrapper != null) {
                return getLocale(wrapper);
            }
            return DEFAULT;
        } catch (Exception e) {
            CorgiLogger.dangerMessage("Error when reading entity:\n");
            e.printStackTrace();
        }
        return DEFAULT;
    }

    @Nonnull
    public static LanguageObject getLocale(Language language) {
        for (LanguageObject locale : LANGS) {
            if (locale.getLanguage().equals(language)) {
                return locale;
            }
        }
        return DEFAULT;
    }

    public static String format(@Nonnull String message, Object... args) {
        int num = 0;
        Object[] arguments = new Object[args.length];
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            arguments[num++] = arg.toString();
        }

        try {
            return MessageFormat.format(message.replace("'", "''"), arguments);
        } catch (IllegalArgumentException ex) {
            return message;
        }
    }

}
