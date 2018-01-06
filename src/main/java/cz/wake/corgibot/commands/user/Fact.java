package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.data.DataManager;
import cz.wake.corgibot.utils.data.SimpleData;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Random;

@SinceCorgi(version = "0.4")
public class Fact implements ICommand {

    public static final DataManager<List<String>> facts = new SimpleData("facts.txt");

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Náhodný fakt dne :trophy:", null).setDescription(facts.get().get(new Random().nextInt(facts.get().size() - 1))).build()).queue();
    }

    @Override
    public String getCommand() {
        return "fact";
    }

    @Override
    public String getDescription() {
        return "Fakty na každý den.";
    }

    @Override
    public String getHelp() {
        return "%fact";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
