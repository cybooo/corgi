package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.lang.management.ManagementFactory;

public class UptimeCommand implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Uptime", null).setDescription(":stopwatch: | " + String.format("%d dní, %02d hodin, %02d minut", days, hours % 24, minutes % 60)).build()).queue();
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
        return ".uptime";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.ALL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
