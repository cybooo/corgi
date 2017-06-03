package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class GitCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        channel.sendMessage(MessageUtils.getEmbed(sender, ColorSelector.getRandomColor())
                .addField("Zde najdeš můj Source!", "https://git.waked.cz/craftmania-cz/bots/corgibot",
                        true).build()).queue();
    }

    @Override
    public String getCommand() {
        return "git";
    }

    @Override
    public String getDescription() {
        return "Odkaz na git";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
