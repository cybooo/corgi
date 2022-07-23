package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
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
        name = "support",
        aliases = {"podpora"},
        description = "commands.support.description",
        help = "commands.support.help",
        category = CommandCategory.GENERAL
)
public class Support implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(I18n.getLoc(gw, "commands.support.embed-title"))
                .setDescription(I18n.getLoc(gw, "commands.support.embed-description").replace("{1}", "https://discord.gg/pR2tj432NS")).build()).queue();
    }

}
