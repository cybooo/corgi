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

import java.io.File;

@SinceCorgi(version = "1.0")
public class Log implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            try {
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Vygenerovaný log (latest.log) by vyžádán!").build()).queue();
                File log = new File("latest.log");
                channel.sendFile(log, "latest.log").queue();
            } catch (Exception e) {
                //
            }
        }
    }

    @Override
    public String getCommand() {
        return "log";
    }

    @Override
    public String getDescription() {
        return "Zaslání logu";
    }

    @Override
    public String getHelp() {
        return "%log";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.BOT_OWNER;
    }

    @Override
    public boolean isOwner() {
        return true;
    }
}
