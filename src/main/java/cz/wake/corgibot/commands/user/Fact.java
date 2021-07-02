package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@SinceCorgi(version = "2.3.2")
public class Fact implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage("This command is currently unavailable!").queue();
        //channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Random fact :trophy:", null).setDescription(CorgiBot.getInstance().getSql().getRandomFact()).build()).queue();
    }

    @Override
    public String getCommand() {
        return "fact";
    }

    @Override
    public String getDescription() {
        return "Facts every day.";
    }

    @Override
    public String getHelp() {
        return "%fact - Generate random fact.";
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
