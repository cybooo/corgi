package cz.wake.corgibot.utils;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ColorSelector {

    private static int getColorCode() {
        int choice = ThreadLocalRandom.current().nextInt(0, 1 + 1);
        return switch (switch (choice) {
            case 0 -> ThreadLocalRandom.current().nextInt(200, 250 + 1);
            case 1 -> ThreadLocalRandom.current().nextInt(100, 150 + 1);
            default -> ThreadLocalRandom.current().nextInt(0, 1 + 1);
        }) {
            case 0 -> ThreadLocalRandom.current().nextInt(200, 250 + 1);
            case 1 -> ThreadLocalRandom.current().nextInt(100, 150 + 1);
            default -> ThreadLocalRandom.current().nextInt(0, 1 + 1);
        };
    }

    public static Color getRandomColor() {
        return new Color(getColorCode(), getColorCode(), getColorCode());
    }
}
