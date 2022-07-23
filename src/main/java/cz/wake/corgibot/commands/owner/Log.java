package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.File;

@OnlyOwner
@CommandInfo(
        name = "log",
        description = "Request logs",
        help = "%log",
        category = CommandCategory.BOT_OWNER
)
@SinceCorgi(version = "1.0")
public class Log implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            try {
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Generated log (latest.log) requested!").build()).queue();
                File log = new File("logs/latest.log");
                channel.sendFile(log, "latest.log").queue();
            } catch (Exception e) {
                //
            }
        }
    }
}
