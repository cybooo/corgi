package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

public class TextToBlock implements Command {


    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length == 0) {
            MessageUtils.sendErrorMessage("Musíš napsat nějaký text!", channel);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String a : StringUtils.join(args, " ").split("")) {
            if (Character.isLetter(a.toLowerCase().charAt(0))) {
                sb.append(":regional_indicator_").append(a.toLowerCase()).append(":");
            } else {
                if (" ".equals(a)) {
                    sb.append(" ");
                }
                sb.append(a);
            }
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setDescription(sb.toString()).build()).queue();

    }

    @Override
    public String getCommand() {
        return "ttb";
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
