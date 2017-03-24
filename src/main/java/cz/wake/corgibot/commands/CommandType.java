package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;

import java.util.List;

public enum CommandType {

    GENERAL,
    MODERATION(false),
    MUSIC(false),
    WAKE;

    private boolean dms;

    CommandType() {
        this(true);
    }
    CommandType(boolean dms) {
        this.dms = dms;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, MODERATION, MUSIC};
    }

    public List<Command> getCommands() {
        return CorgiBot.getInstance().getCommandHandler().getCommandsByType(this);
    }

    public String formattedName() {
        return toString();
    }

    public boolean usableInDMs() {
        return dms;
    }
}
