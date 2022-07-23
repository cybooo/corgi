package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.CPUDaemon;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@CommandInfo(
        name = "botstats",
        description = "commands.botstats.description",
        help = "commands.botstats.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.3.0")
public class BotStats implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription(I18n.getLoc(gw, "commands.botstats.awake") + " " + getUptime(gw));
            embed.addField(EmoteList.HOMES + " " + I18n.getLoc(gw, "commands.botstats.guilds"), String.valueOf(channel.getJDA().getGuilds().size()), true);
            embed.addField(EmoteList.USERS + " " + I18n.getLoc(gw, "commands.botstats.users"), String.valueOf(channel.getJDA().getUsers().size()), true);
            embed.addField(EmoteList.PENCIL + " " + I18n.getLoc(gw, "commands.botstats.text-channels"), String.valueOf(channel.getJDA().getTextChannels().size()), true);
            embed.addField(EmoteList.MEGAFON + " " + I18n.getLoc(gw, "commands.botstats.voice-channels"), String.valueOf(channel.getJDA().getVoiceChannels().size()), true);
            embed.addField(EmoteList.COMMANDS + " " + I18n.getLoc(gw, "commands.botstats.commands-run"), String.valueOf(CorgiBot.commands), true);
            embed.addField(EmoteList.JDA + " " + I18n.getLoc(gw, "commands.botstats.jda-version"), JDAInfo.VERSION, true);
            embed.addField(EmoteList.COMPRESS + " " + I18n.getLoc(gw, "commands.botstats.load-cpu"), ((int) (CPUDaemon.get() * 10000)) / 100d + "%", true);
            embed.addField(EmoteList.COMET + " " + I18n.getLoc(gw, "commands.botstats.threads"), String.valueOf(Thread.getAllStackTraces().size()), true);
            embed.addField(EmoteList.JAVA + " " + I18n.getLoc(gw, "commands.botstats.java"), System.getProperty("java.version"), true);
            embed.setAuthor(I18n.getLoc(gw, "commands.botstats.title"), null, CorgiBot.getShardManager().getShards().get(0).getSelfUser().getAvatarUrl());
            embed.setColor(Constants.BLUE);
            channel.sendMessageEmbeds(embed.build()).queue();
        }

    }

    private String getUptime(GuildWrapper gw) {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return String.format(I18n.getLoc(gw, "commands.uptime.embed-description"), days, hours % 24, minutes % 60);
    }

    private String getCPULoad() {
        String format = "-1";
        try {
            format = Double.toString(this.cpuLoad());
        } catch (Exception ignored) {
        }
        format = format.replaceAll("0.", "");
        return format + "%";
    }

    private double cpuLoad() throws Exception {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        final ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        final AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
        if (list.isEmpty()) {
            return Double.NaN;
        }
        final Attribute att = (Attribute) list.get(0);
        final Double value = (Double) att.getValue();
        if (value == -1.0) {
            return Double.NaN;
        }
        return (int) (value * 1000.0) / 10.0;
    }
}
