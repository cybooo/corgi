package cz.wake.corgibot.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public interface Command {

    void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member);

    String getCommand();

    String getDescription();

    CommandType getType();

    default boolean deleteMessage() {
        return true;
    }

}
