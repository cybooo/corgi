package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Random;

@CommandInfo(
        name = "choose",
        description = "If you do not know what to select? Corgi will choose something for you!",
        help = "%choose question_1 ; question_2 ; question_3",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "3.3.0")
public class Choose implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Example: c!choose cat ; dog", channel);
        } else {
            // Format message
            String request = message.getContentRaw().replaceAll("\\s+\\;", ";").replaceAll("\\;\\s+", ";").replaceAll("\\;", ";").replace("choose ", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\;");
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage("You need to provide more than 1 option!", channel);
                return;
            }
            if (arguments[0].equalsIgnoreCase("choose")) {
                MessageUtils.sendErrorMessage("The first option was entered incorrectly. Please try again..", channel);
                return;
            }
            MessageUtils.getEmbed(Constants.BLUE).setDescription(getRandomThinkingEmote() + " | **" + member.getUser().getName() + "**, " + "i chose" + " **" + arguments[(int) (Math.random() * arguments.length)] + "**!");
        }
    }

    private String getRandomThinkingEmote() {
        Random r = new Random();
        int number = r.nextInt(3) + 1;
        return switch (number) {
            case 1 -> EmoteList.THINKING_1;
            case 2 -> EmoteList.THINKING_2;
            case 3 -> EmoteList.THINKING_3;
            default -> EmoteList.THINKING_1;
        };
    }
}
