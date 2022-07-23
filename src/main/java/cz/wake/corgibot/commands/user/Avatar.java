package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
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

@CommandInfo(
        name = "avatar",
        description = "commands.avatar.description",
        help = "commands.avatar.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.2")
public class Avatar implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String id;
        if (args.length != 1) {
            id = member.getUser().getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "internal.required.mention"), 10000, channel);
            return;
        }
        Member member1 = message.getGuild().getMemberById(id);
        if (member1 == null) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "internal.error.user-not-found"), 10000, channel);
            return;
        }
        String url = member1.getUser().getEffectiveAvatarUrl() + "?size=1024";
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setTitle(member1.getUser().getName())
                .setImage(url).build()).queue();
    }

}
