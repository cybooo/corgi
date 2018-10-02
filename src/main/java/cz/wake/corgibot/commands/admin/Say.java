package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

/*@SinceCorgi(version = "0.8")
public class Say implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Nelze poslat zprávu, která nemá text!", channel);
            return;
        }
        channel.sendMessage(message.getContentRaw().replace(gw.getPrefix() + "say", "")).queue();
    }

    @Override
    public String getCommand() {
        return "say";
    }

    @Override
    public String getDescription() {
        return "Tímto příkazem lze psát jako bot.";
    }

    @Override
    public String getHelp() {
        return "%say <text>";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTARTOR;
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }
}*/
