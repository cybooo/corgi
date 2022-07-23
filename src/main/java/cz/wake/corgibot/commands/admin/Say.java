package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "say",
        help = "commands.say.help",
        description = "commands.say.description",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "0.8")
public class Say implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.no-context"), channel);
            return;
        }
        channel.sendMessage(message.getContentRaw().replace(gw.getPrefix() + "say", "")).queue();
    }


    @Override
    public boolean deleteMessage() {
        return true;
    }

}
