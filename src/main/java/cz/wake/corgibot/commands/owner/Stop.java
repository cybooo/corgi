package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

@SinceCorgi(version = "0.1")
public class Stop implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (member.getUser().getId().equals("177516608778928129")) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription(":skull_crossbones: | Vyžádáno vypnutí! Vypínám se...").build()).queue();
            System.exit(0);
        }
    }

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Tento příkaz vypne bota. (Pouze Wake)";
    }

    @Override
    public String getHelp() {
        return "%stop";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.BOT_OWNER;
    }
}
