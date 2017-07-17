package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class SayCommand implements Command {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(message.getRawContent().replace(".say", "")).queue();
    }

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GUILD_OWNER;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.GUILD_OWNER;
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
