package cz.wake.corgibot.commands;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public interface ICommand {

    void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw);

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
