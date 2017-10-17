package cz.wake.corgibot.utils;

import net.dv8tion.jda.core.entities.User;

public class FormatUtil {

    /**
     * Removes @everyone & @here
     *
     * @param input String to filter
     * @return Filtered string
     */
    public static String filterEveryone(String input) {
        return input.replace("@everyone", "**@**everyone").replace("@here", "**@**here");
    }

    /**
     * Formats an User's Name & Discriminator
     *
     * @param user User to format
     * @return Formatted String
     */
    public static String formatUser(User user) {
        return filterEveryone("**" + user.getName() + "**#" + user.getDiscriminator());
    }

    /**
     * Formats an User's Name, Discriminator & ID
     *
     * @param user User to format
     * @return Formatted String
     */
    public static String formatFullUser(User user) {
        return filterEveryone("**" + user.getName() + "**#" + user.getDiscriminator() + "(ID: " + user.getId() + ")");
    }

    /**
     * Filters out any formatting characters.
     *
     * @param in String to format
     * @return Formatted String
     */
    public static String filterMarkdown(String in) {
        return in.replace("*", "\\*").replace("`", "\\`").replace("_", "\\_").replace("~", "\\~");
    }

    /**
     * Sets the highlightjs language for the given String.
     *
     * @param in String to give highlighting to.
     * @param language Language to highlight in.
     * @return Formatted String
     */
    public static String setHighlightLanguage(String in, String language) {
        if (in.length() <= 2000 - language.length() + 8)
            return "```" + language + "\n" + in + "```";
        else
            return "```" + language + "\n" + in.substring(0, in.length() - language.length() - 8) + "```";
    }
}
