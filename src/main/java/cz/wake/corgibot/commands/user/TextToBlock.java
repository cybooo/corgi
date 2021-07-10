package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

@CommandInfo(
        name = "ttb",
        description = "Text written in blocks",
        help = "%ttb <text>",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "0.9")
public class TextToBlock implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 0) {
            MessageUtils.sendErrorMessage("You need to provide some text!", channel);
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
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(sb.toString()).build()).queue();

    }

}
