package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
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

import java.util.concurrent.ThreadLocalRandom;

@CommandInfo(
        name = "8ball",
        aliases = {"8b"},
        description = "Ask if this is true or not!",
        help = "%8ball <question>",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "0.3")
public class EightBall implements CommandBase {

    private static final String[] outcomes =
            {
                    "Yes.", "No.", "Most likely YES!", "Maybe.", "Let me think.. YES!", "Probably not!", "Unlikely..", "\n" +
                    "When you think about it, it's possible!", "It's definitely like that", "Definitely yes", "\n" +
                    "Something tells me no"
            };

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            if (args.length < 1) {
                channel.sendMessage(member.getUser().getAsMention() + " You need to ask me something, i can't read your mind!").queue();
            } else {
                channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.PINK).addField(member.getUser().getName() + " is asking:", message.getContentRaw().replace("8ball ", "").replace("8b", "").replace(gw.getPrefix(), ""), false).addField("Corgi responds:", outcomes[ThreadLocalRandom.current().nextInt(0, outcomes.length)], false).build()).queue();
            }
        } catch (Exception e) {
            MessageUtils.sendAutoDeletedMessage("Something went wrong!", 10000, channel);
            CorgiBot.LOGGER.error("Something went wrong while executing " + gw.getPrefix() + "8ball!", e);
        }
    }

}
