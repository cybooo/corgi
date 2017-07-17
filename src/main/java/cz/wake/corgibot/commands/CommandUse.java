package cz.wake.corgibot.commands;

public enum CommandUse {

    GUILD,
    PRIVATE,
    ALL;

    CommandUse() {
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandUse[] getUseType() {
        return new CommandUse[]{GUILD, PRIVATE, ALL};
    }

    public String formattedName() {
        return toString();
    }
}
