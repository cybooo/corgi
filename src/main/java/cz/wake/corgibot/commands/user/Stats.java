package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.objects.user.UserGuildData;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "stats",
        description = "Displays your stats",
        help = "%stats",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.3.9")
public class Stats implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        UserGuildData userGuildData = BotManager.getUserWrappers().get(member.getId()).getGuildData().get(gw.getGuildId());
        message.replyEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle("Your stats")
                        .addField("XP", String.valueOf(userGuildData.getXp()), true)
                        //.addField("Level", String.valueOf(userGuildData.getLevel()), true)
                        .addField("Messages", String.valueOf(userGuildData.getMessages()), true)
                        //.addField("Voice minutes", String.valueOf(userGuildData.getVoiceTime()), true)
                .build()).queue();
    }

}
