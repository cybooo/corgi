package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.EmoteList;

import java.util.List;

public enum CommandCategory {

    GENERAL(EmoteList.CLIPBOARD),
    MODERATION(EmoteList.MODERATION),
    GAMES(EmoteList.VIDEO_GAME),
    FUN(EmoteList.COMET),
    MUSIC,
    ADMINISTRATOR(EmoteList.PLAYING_CARD),
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
        return new CommandCategory[]{GENERAL, GAMES, FUN, MODERATION, ADMINISTRATOR, MUSIC, HIDDEN, BOT_OWNER};
    }

    public List<FinalCommand> getCommands() {
        return CorgiBot.getInstance().getCommandManager().getCommandsByCategory(this);
    }

    public String formattedName() {
        return toString();
    }

    public String getEmote() {
        return emote;
    }
}
