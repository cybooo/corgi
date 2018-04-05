package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.EmoteList;

import java.util.List;

public enum CommandCategory {

    GENERAL(EmoteList.CLIPBOARD),
    MODERATION(EmoteList.MODERATION),
    GAMES,
    FUN(EmoteList.VIDEO_GAME),
    MUSIC,
    ADMINISTARTOR(EmoteList.PLAYING_CARD),
    HIDDEN,
    BOT_OWNER;

    private String emote;

    CommandCategory() {
    }

    CommandCategory(String e) {
        this.emote = e;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CommandCategory[] getTypes() {
        return new CommandCategory[]{GENERAL, FUN, MODERATION, ADMINISTARTOR, MUSIC, BOT_OWNER};
    }

    public List<Command> getCommands() {
        return CorgiBot.getInstance().getCommandHandler().getCommandsByType(this);
    }

    public String formattedName() {
        return toString();
    }

    public String getEmote() {
        return emote;
    }
}
