package cz.wake.corgibot.commands;

import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public interface ICommand {

    void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix);

    String getCommand();

    String getDescription();

    String getHelp();

    CommandType getType();

    Rank getRank();

    default String[] getAliases() {
        return new String[]{};
    }

    default boolean deleteMessage() {
        return false;
    }
}
