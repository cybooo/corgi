package cz.wake.corgibot.utils;

import cz.wake.corgibot.CorgiBot;

public class CorgiLogger {

    public static void greatMessage(final String text){
        CorgiBot.LOGGER.info("[" + AnsiColor.GREEN.applyTo("✓") + "] " + text);
    }

    public static void warnMessage(final String text){
        CorgiBot.LOGGER.info("[" + AnsiColor.YELLOW.applyTo("!") + "] " + text);
    }

    public static void dangerMessage(final String text){
        CorgiBot.LOGGER.info("[" + AnsiColor.RED.applyTo("X") + "] " + text);
    }

    public static void infoMessage(final String text){
        CorgiBot.LOGGER.info("[" + AnsiColor.BLUE.applyTo("?") + "] " + text);
    }


}
