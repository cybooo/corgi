package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

@SinceCorgi(version = "0.9")
public class TextToBlock implements Command {


    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
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
        return "Text psaný v blocích.";
    }

    @Override
    public String getHelp() {
        return "%ttb <text>";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }
}
