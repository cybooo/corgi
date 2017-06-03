package cz.wake.corgibot.utils;

public class TimeUtils {

    public static String formatTime(String format, long time, boolean addPadding) {
        long days = 0;
        long hours = 0;
        long minutes = 0;
        if ((format.contains("%d")) && (time >= 1440)) {
            days = getDifference(time, 1440);
            time -= 1440 * days;
        }
        if ((format.contains("%h")) && (time >= 60)) {
            hours = getDifference(time, 60);
            time -= 60 * hours;
        }
        minutes = time;

        format = format.replace("%d", asString(days, addPadding));
        format = format.replace("%h", asString(hours, addPadding));
        format = format.replace("%m", asString(minutes, addPadding));
        return format;
    }

    private static String asString(long time, boolean addPadding) {
        if ((time < 10) && (addPadding)) {
            return "0" + time;
        }
        return Long.valueOf(time).toString();
    }

    private static int getDifference(long time, int secDiff) {
        long newTime = time / secDiff;
        return (int) newTime;
    }

}
