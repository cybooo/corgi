package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.lang.management.ManagementFactory;

@CommandInfo(
        name = "uptime",
        description = "commands.uptime.description",
        help = "commands.uptime.help",
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
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(I18n.getLoc(gw, "commands.uptime.embed-title"), null).setDescription(":stopwatch: | " + String.format(I18n.getLoc(gw, "commands.uptime.embed-description"), days, hours % 24, minutes % 60)).build()).queue();
    }

}
