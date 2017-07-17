package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;

import java.util.List;

public enum CommandType {

    GENERAL,
    MODERATION,
    FUN,
    MUSIC,
    GUILD_OWNER,
    BOT_OWNER;

    CommandType() {
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandType[] getTypes() {
        return new CommandType[]{GENERAL, MODERATION, MUSIC, FUN, GUILD_OWNER, BOT_OWNER};
    }

    public List<Command> getCommands() {
        return CorgiBot.getInstance().getCommandHandler().getCommandsByType(this);
    }

    public String formattedName() {
        return toString();
    }
}
