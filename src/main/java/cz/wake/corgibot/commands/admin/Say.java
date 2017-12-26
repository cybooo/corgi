package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import net.dv8tion.jda.core.entities.*;

public class Say implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(message.getRawContent().replace(guildPrefix + "say", "")).queue();
    }

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public String getDescription() {
        return "Tímto příkazem lze psát jako bot.";
    }

    @Override
    public String getHelp() {
        return  "%say <text>";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTARTOR;
    }

    @Override
    public Rank getRank() {
        return Rank.ADMINISTRATOR;
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
