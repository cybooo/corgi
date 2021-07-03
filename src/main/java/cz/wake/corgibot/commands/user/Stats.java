package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
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
        name = "stats",
        description = "Global statistics of Corgi",
        help = "%stats - Show all statistics of Corgi",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.3.0")
public class Stats implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            long totalMb = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
            long usedMb = (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("My uptime is " + getUptime());
            embed.addField(EmoteList.HOMES + " Guilds", String.valueOf(channel.getJDA().getGuilds().size()), true);
            embed.addField(EmoteList.USERS + " Users", String.valueOf(channel.getJDA().getUsers().size()), true);
            embed.addField(EmoteList.PENCIL + " Text channels", String.valueOf(channel.getJDA().getTextChannels().size()), true);
            embed.addField(EmoteList.MEGAFON + " Voice channels", String.valueOf(channel.getJDA().getVoiceChannels().size()), true);
            embed.addField(EmoteList.COMMANDS + " Executed commands", String.valueOf(CorgiBot.commands), true);
            embed.addField(EmoteList.JDA + " JDA Version", JDAInfo.VERSION, true);
            embed.addField(EmoteList.COMPRESS + " CPU Load", getCPULoad(), true);
            // embed.addField(EmoteList.FLOPY_DISC + " RAM", usedMb + "MB / " + totalMb + "MB", true); - Inaccurate. Will find another way to calculate
            embed.addField(EmoteList.COMET + " Threads", String.valueOf(Thread.getAllStackTraces().size()), true);
            embed.addField(EmoteList.JAVA + " Java", System.getProperty("java.version"), true);
            embed.setAuthor("Corgi's statistics", null, CorgiBot.getJda().getSelfUser().getAvatarUrl());
            embed.setColor(Constants.BLACK);
            channel.sendMessage(embed.build()).queue();
        }

    }

    private String getUptime() {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return String.format("%d days, %02d hours, %02d minutes", days, hours % 24, minutes % 60);
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
