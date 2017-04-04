package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class HelpCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        channel.sendMessage(MessageUtils.getEmbed(new Color(58, 95, 205)).setTitle("Zkontroluj si zprávy", null).setDescription(":mailbox_with_mail: | Odeslal jsem ti do zpráv nápovědu s příkazy!").build()).queue();
        sender.openPrivateChannel().queue(msg -> {
            msg.sendMessage(MessageUtils.getEmbed(sender).setColor(new Color(58, 95, 205))
                    .setTitle("**Nápověda k CorgiBot**", null).setDescription("**.git** - Odkaz na můj source\n" +
                            "**.8ball** - Zkouška pravdy ANO/NE\n").build()).queue();
        });
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Vypis vsech prikazu.";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
