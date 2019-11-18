package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.CPUDaemon;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.lang.management.ManagementFactory;

@SinceCorgi(version = "1.3.0")
public class Stats implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            long totalMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription(I18n.getLoc(gw, "commands.stats.awake") + " " + getUptime());
            embed.addField(EmoteList.HOMES + " " + I18n.getLoc(gw, "commands.stats.guilds"), String.valueOf(channel.getJDA().getGuilds().size()), true);
            embed.addField(EmoteList.USERS + " " + I18n.getLoc(gw, "commands.stats.users"), String.valueOf(channel.getJDA().getUsers().size()), true);
            embed.addField(EmoteList.PENCIL + " " + I18n.getLoc(gw, "commands.stats.text-channels"), String.valueOf(channel.getJDA().getTextChannels().size()), true);
            embed.addField(EmoteList.MEGAFON + " " + I18n.getLoc(gw, "commands.stats.voice-channels"), String.valueOf(channel.getJDA().getVoiceChannels().size()), true);
            embed.addField(EmoteList.COMMANDS + " " + I18n.getLoc(gw, "commands.stats.commands-run"), String.valueOf(CorgiBot.commands), true);
            embed.addField(EmoteList.JDA + " " + I18n.getLoc(gw, "commands.stats.jda-version"), JDAInfo.VERSION, true);
            embed.addField(EmoteList.COMPRESS + " " + I18n.getLoc(gw, "commands.stats.load-cpu"), ((int) (CPUDaemon.get() * 10000)) / 100d + "%", true);
            embed.addField(EmoteList.FLOPY_DISC + " " + I18n.getLoc(gw, "commands.stats.memory"), usedMb + "MB / " + totalMb + "MB", true);
            embed.addField(EmoteList.COMET + " " + I18n.getLoc(gw, "commands.stats.threads"), String.valueOf(Thread.getAllStackTraces().size()), true);
            embed.addField(EmoteList.JAVA + " " + I18n.getLoc(gw, "commands.stats.java"), System.getProperty("java.version"), true);
            embed.setAuthor(I18n.getLoc(gw, "commands.stats.title"), null, CorgiBot.getJda().getSelfUser().getAvatarUrl());
            embed.setColor(Constants.BLACK);
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
