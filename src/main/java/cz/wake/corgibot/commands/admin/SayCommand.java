package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class SayCommand implements Command {


    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(message.getRawContent().replace(".say", "")).queue();
    }

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.WAKE;
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
