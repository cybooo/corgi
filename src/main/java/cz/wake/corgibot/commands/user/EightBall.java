package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ThreadLocalRandom;

@CommandInfo(
        name = "8ball",
        aliases = {"8b"},
        description = "commands.eight-ball.description",
        help = "commands.eight-ball.help",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "0.3")
public class EightBall implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            if (args.length < 1) {
                channel.sendMessage(String.format(I18n.getLoc(gw, "commands.eight-ball.ask-something"), member.getUser().getAsMention())).queue();
            } else {
                String[] outcomes = I18n.getLoc(gw, "commands.eight-ball.outcomes").split(";");
                channel.sendMessageEmbeds(MessageUtils.getEmbed(member.getUser(), Constants.PINK)
                        .addField(String.format(I18n.getLoc(gw, "commands.eight-ball.is-asking"), member.getUser().getName()), message.getContentRaw().replace("8ball ", "").replace("8b", "").replace(gw.getPrefix(), ""), false)
                        .addField(I18n.getLoc(gw, "commands.eight-ball.responds"), outcomes[ThreadLocalRandom.current().nextInt(0, outcomes.length)], false)
                        .build()).queue();
            }
        } catch (Exception e) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "internal.error.command-failed"), 10000, channel);
            CorgiBot.LOGGER.error("Something went wrong while executing " + gw.getPrefix() + "8ball!", e);
        }
    }

}
