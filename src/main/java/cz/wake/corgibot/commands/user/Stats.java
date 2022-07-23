package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.objects.user.UserGuildData;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "stats",
        description = "commands.stats.description",
        help = "commands.stats.help",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.3.9")
public class Stats implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        UserGuildData userGuildData = BotManager.getUserWrappers().get(member.getId()).getGuildData().get(gw.getGuildId());
        message.replyEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                .setTitle(I18n.getLoc(gw, "commands.stats.embed-title"))
                        .addField(I18n.getLoc(gw, "commands.stats.embed-xp"), String.valueOf(userGuildData.getXp()), true)
                        //.addField(I18n.getLoc(gw, "commands.stats.level"), String.valueOf(userGuildData.getLevel()), true)
                        .addField(I18n.getLoc(gw, "commands.stats.messages"), String.valueOf(userGuildData.getMessages()), true)
                        //.addField(I18n.getLoc(gw, "commands.stats.voice-minutes"), String.valueOf(userGuildData.getVoiceTime()), true)
                .build()).queue();
    }

}
