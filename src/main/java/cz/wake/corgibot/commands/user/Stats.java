package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.CPUDaemon;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.lang.management.ManagementFactory;

@SinceCorgi(version = "1.3.0")
public class Stats implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            long totalMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription("Aktuálně jsem vzhůru již " + getUptime());
            embed.addField(EmoteList.HOMES + " Guilds", String.valueOf(channel.getJDA().getGuilds().size()), true);
            embed.addField(EmoteList.USERS + " Počet uživatelů", String.valueOf(channel.getJDA().getUsers().size()), true);
            embed.addField(EmoteList.PENCIL + " Text channelů", String.valueOf(channel.getJDA().getTextChannels().size()), true);
            embed.addField(EmoteList.MEGAFON + " Voice channelů", String.valueOf(channel.getJDA().getVoiceChannels().size()), true);
            embed.addField(EmoteList.COMMANDS + "Provedeno příkazů", String.valueOf(CorgiBot.commands), true);
            embed.addField(EmoteList.JDA + " JDA verze", JDAInfo.VERSION, true);
            embed.addField(EmoteList.COMPRESS + " Zatížení", ((int) (CPUDaemon.get() * 10000)) / 100d + "%", true);
            embed.addField(EmoteList.FLOPY_DISC + " Paměť", usedMb + "MB / " + totalMb + "MB", true);
            embed.addField(EmoteList.COMET + " Threads", String.valueOf(Thread.getAllStackTraces().size()), true);
            embed.addField(EmoteList.JAVA + " Java", Runtime.class.getPackage().getImplementationVersion(), true);
            embed.setAuthor("Corgiho statistiky", null, CorgiBot.getJda().getSelfUser().getAvatarUrl());
            embed.setColor(Constants.BLUE);
            channel.sendMessage(embed.build()).queue();
        }

    }

    @Override
    public String getCommand() {
        return "stats";
    }

    @Override
    public String getDescription() {
        return "Globální statistiky Bota a jeho verze.";
    }

    @Override
    public String getHelp() {
        return "%stats - Zobrazení všech statistik o Corgim.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    private String getUptime() {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return String.format("%d dní, %02d hodin, %02d minut", days, hours % 24, minutes % 60);
    }
}
