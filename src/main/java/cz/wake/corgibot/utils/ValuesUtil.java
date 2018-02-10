package cz.wake.corgibot.utils;

public class ValuesUtil {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }


}
