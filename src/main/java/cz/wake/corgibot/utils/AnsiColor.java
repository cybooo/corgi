package cz.wake.corgibot.utils;

public enum AnsiColor {
    /**
     * Color BLACK.
     */
    BLACK("30"),
    /**
     * Color RED.
     */
    RED("31"),
    /**
     * Color GREEN.
     */
    GREEN("32"),
    /**
     * Color YELLOW.
     */
    YELLOW("33"),
    /**
     * Color BLUE.
     */
    BLUE("34"),
    /**
     * Color MAGENTA.
     */
    MAGENTA("35"),
    /**
     * Color CYAN.
     */
    CYAN("36"),
    /**
     * Color WHITE.
     */
    WHITE("37");

    static final String ESCAPE_CODE_PATTERN = "\u001B[%sm";
    static final String ESCAPE_CODE_RESET = "\u001B[0m";

    final String escapeCode;

    AnsiColor(String color) {
        this.escapeCode = String.format(ESCAPE_CODE_PATTERN, color);
    }

    /**
     * Applies the selected color to a given string.
     *
     * @param string the string to be colored.
     * @return a {@link java.lang.String} escaped by corresponding ANSI codes.
     */
    public String applyTo(String string) {
        return escapeCode + string + ESCAPE_CODE_RESET;
    }
}
