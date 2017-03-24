package cz.wake.corgibot.utils;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ColorSelector {

    private static int getColorCode() {
        int choice = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        switch (choice) {
            case 0:
                choice = ThreadLocalRandom.current().nextInt(200, 250 + 1);
                break;
            case 1:
                choice = ThreadLocalRandom.current().nextInt(100, 150 + 1);
                break;
        }
        return choice;
    }

    public static Color getRandomColor() {
        return new Color(getColorCode(), getColorCode(), getColorCode());
    }
}
