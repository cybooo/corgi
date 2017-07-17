package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class StopCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription(":skull_crossbones: | Vyžádáno vypnutí! Vypínám se...").build()).queue();
        System.exit(0);
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.ALL;
    }

    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
