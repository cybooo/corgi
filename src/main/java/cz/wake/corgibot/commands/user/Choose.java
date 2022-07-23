package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Random;

@CommandInfo(
        name = "choose",
        description = "commands.choose.description",
        help = "commands.choose.help",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "3.3.0")
public class Choose implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.choose.must-select"), channel);
        } else {
            // Format message
            String request = message.getContentRaw().replaceAll("\\s+\\;", ";").replaceAll("\\;\\s+", ";").replaceAll("\\;", ";").replace("choose ", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\;");
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.choose.no-arguments"), channel);
                return;
            }
            if (arguments[0].equalsIgnoreCase("choose")) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.choose.choose-in-choose"), channel);
                return;
            }
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(getRandomThinkingEmote() + " | **" + member.getUser().getName() + "**, " + I18n.getLoc(gw, "commands.choose.corgi-select") + " **" + arguments[(int) (Math.random() * arguments.length)] + "**!").build()).queue();
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
