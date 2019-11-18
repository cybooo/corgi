package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.lang.management.ManagementFactory;

@SinceCorgi(version = "0.2")
public class Uptime implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Uptime", null).setDescription(":stopwatch: | " + String.format("%d dní, %02d hodin, %02d minut", days, hours % 24, minutes % 60)).build()).queue();
    }

    @Override
    public String getCommand() {
        return "uptime";
    }

    @Override
    public String getDescription() {
        return "Informace o tom, jak dlouho běží bot.";
    }

    @Override
    public String getHelp() {
        return "%uptime - Zobrazí čas od spuštění.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }
}
