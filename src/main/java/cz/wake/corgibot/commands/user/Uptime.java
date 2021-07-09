package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.lang.management.ManagementFactory;

@CommandInfo(
        name = "uptime",
        description = "Get Corgi's uptime",
        help = "%uptime - Shows Corgi's uptime",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.2")
public class Uptime implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle("Uptime", null).setDescription(":stopwatch: | " + String.format("%d days, %02d hours, %02d minutes", days, hours % 24, minutes % 60)).build()).queue();
    }

}
