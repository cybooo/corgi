package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "support",
        aliases = {"podpora"},
        description = "Join Corgi's support server",
        help = "%support - Get the URL",
        category = CommandCategory.GENERAL
)
public class Support implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle("Corgi's support server")
                .setDescription("To connect to the support server, [**click here!**]({1})".replace("{1}", "https://discord.gg/pR2tj432NS")).build()).queue();
    }

}
