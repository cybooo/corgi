package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
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

@CommandInfo(
        name = "invite",
        aliases = {"inviteme"},
        description = "Invite me to your server!",
        help = "%invite - Sends a link to invite Corgi to your server.",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.0")
public class Invite implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle("Invite me to your server!")
                .setDescription("To invite me, [**click here!**](https://discord.com/api/oauth2/authorize?client_id=860244075138383922&permissions=3220700791&scope=bot)").build()).queue();
    }

}
