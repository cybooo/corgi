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
        name = "about",
        aliases = {"info", "binfo", "corgi"},
        description = "commands.about.description",
        help = "commands.about.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.1")
public class About implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(I18n.getLoc(gw, "commands.about.embed-description"))
                .setDescription(
                        I18n.getLoc(gw, "commands.about.embed-description").formatted(channel.getJDA().getUserById("485434705903222805").getAsMention()))
                .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl())
                .setImage("https://corgibot.xyz/assets/img/corgilogo.png").build()).queue();

    }

}
