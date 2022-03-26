package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "disableslashnotice",
        description = "Disables the slash commands notice.",
        help = "%disableslashnotice",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}

)
public class DisableSlashNotice implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (!BotManager.DISABLED_SLASH_NOTICES.contains(gw.getGuildId())) {
            BotManager.DISABLED_SLASH_NOTICES.add(gw.getGuildId());
            MessageUtils.sendErrorMessage("Slash notice disabled!", channel);
        } else {
            BotManager.DISABLED_SLASH_NOTICES.remove(gw.getGuildId());
            MessageUtils.sendErrorMessage("Slash notice enabled!", channel);
        }
    }
}
