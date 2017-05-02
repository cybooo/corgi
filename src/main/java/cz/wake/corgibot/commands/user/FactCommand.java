package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.data.DataManager;
import cz.wake.corgibot.utils.data.SimpleData;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class FactCommand implements Command {

    public static final DataManager<List<String>> facts = new SimpleData("facts.txt");

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        channel.sendMessage(MessageUtils.getEmbed(new Color(205, 50,120)).setTitle("Náhodný fakt dne :trophy:", null).setDescription(facts.get().get(new Random().nextInt(facts.get().size() - 1))).build()).queue();
    }

    @Override
    public String getCommand() {
        return "fact";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
