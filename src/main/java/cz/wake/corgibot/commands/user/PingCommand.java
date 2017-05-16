package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class PingCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
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
        return "Zkontrolovani pingu.";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
