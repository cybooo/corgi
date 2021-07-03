package cz.wake.corgibot.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.Beta;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@Beta
@CommandInfo(
        name = "test",
        aliases = {"hmm", "dev", "uf"},
        help = "%test",
        description = "Test command",
        category = CommandCategory.BOT_OWNER,
        userPerms = {Permission.ADMINISTRATOR},
        botPerms = {Permission.MESSAGE_EMBED_LINKS}
)
public class TestCommand implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        message.reply(MessageUtils.getEmbed(Constants.GIVEAWAY_BLUE).setTitle("NICE BRO").setDescription("hm").build()).complete();
    }

}
