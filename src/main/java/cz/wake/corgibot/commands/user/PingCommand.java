package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class PingCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Color.GRAY).setDescription("Vypočítávám ping...").build()).queue(m -> {
            m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":ping_pong: Pong! `" + message.getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + " ms`").build()).queue();
        });
    }

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Zjištění rychlosti odezvy.";
    }

    @Override
    public String getHelp() {
        return  ".ping";
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
