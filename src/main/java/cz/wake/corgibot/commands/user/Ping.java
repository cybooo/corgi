package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.time.temporal.ChronoUnit;

@SinceCorgi(version = "0.1")
public class Ping implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(MessageUtils.getEmbed(Color.GRAY).setDescription("Vypočítávám ping...").build()).queue(m -> {
            m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.PONG + " Pong! `" + message.getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS) + " ms`").build()).queue();
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
        return  "%ping";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

}
