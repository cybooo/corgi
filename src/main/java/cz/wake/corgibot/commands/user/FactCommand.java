package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.data.DataManager;
import cz.wake.corgibot.utils.data.SimpleData;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Random;

public class FactCommand implements Command {

    public static final DataManager<List<String>> facts = new SimpleData("facts.txt");

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Náhodný fakt dne :trophy:", null).setDescription(facts.get().get(new Random().nextInt(facts.get().size() - 1))).build()).queue();
    }

    @Override
    public String getCommand() {
        return "fact";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.ALL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
