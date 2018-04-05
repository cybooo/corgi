package cz.wake.corgibot.utils;

import cz.wake.corgibot.CorgiBot;

public class CorgiLogger {

    public static void greatMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.GREEN.applyTo("✔ success") + " " + text);
    }

    public static void warnMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.YELLOW.applyTo("⚠ warn") + "    " + text);
    }

    public static void dangerMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.RED.applyTo("✖ danger") + "  " + text);
    }

    public static void fatalMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.RED.applyTo("✖ fatal") + "   " + text);
    }

    public static void infoMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.BLUE.applyTo("ℹ info") + "    " + text);
    }

    public static void debugMessage(final String text){
        CorgiBot.LOGGER.info(AnsiColor.MAGENTA.applyTo("… debug") + "   " + text);
    }


}
