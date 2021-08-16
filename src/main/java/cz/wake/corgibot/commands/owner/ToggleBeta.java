package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@OnlyOwner
@CommandInfo(
        name = "togglebeta",
        description = "Toggles if the guild is beta",
        help = "%togglebeta",
        category = CommandCategory.BOT_OWNER
)
@SinceCorgi(version = "1.3.6")
public class ToggleBeta implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        gw.setBeta(!gw.isBeta());
        channel.sendMessage("Beta features have been **" + (gw.isBeta() ? "Enabled" : "Disabled") + "** for this guild!").queue();
    }
}
