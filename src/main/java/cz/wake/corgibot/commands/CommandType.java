package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;

import java.util.List;

public enum CommandType {

    GENERAL,
    MODERATION,
    FUN,
    MUSIC,
    ADMINISTARTOR,
    BOT_OWNER;

    CommandType() {
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, FUN, MODERATION, ADMINISTARTOR, MUSIC, BOT_OWNER};
    }

    public List<ICommand> getCommands() {
        return CorgiBot.getInstance().getCommandHandler().getCommandsByType(this);
    }

    public String formattedName() {
        return toString();
    }
}
