package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.data.DataManager;
import cz.wake.corgibot.utils.data.SimpleData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.Random;

@SinceCorgi(version = "0.4")
public class Fact implements Command {

    public static final DataManager<List<String>> facts = new SimpleData("facts.txt");

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
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
        return "%fact - Vygenerování náhodného faktu.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"fakt"};
    }
}
