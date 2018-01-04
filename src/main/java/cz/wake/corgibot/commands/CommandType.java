package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.EmoteList;

import java.util.List;

public enum CommandType {

    GENERAL(EmoteList.CLIPBOARD),
    MODERATION(EmoteList.MODERATION),
    FUN(EmoteList.VIDEO_GAME),
    MUSIC,
    ADMINISTARTOR(EmoteList.PLAYING_CARD),
    BOT_OWNER;

    private String emote;

    CommandType() {
    }

    CommandType(String e){
        this.emote = e;
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

    public String getEmote() {
        return emote;
    }
}
