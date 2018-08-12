package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.util.regex.Pattern;

public class Embed implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Špatně napsaný příkaz! Př. `" + gw.getPrefix() + "embed <title> | <text> | #B0171F", channel);
            return;
        }

        String str[] = message.getContentRaw().replace(gw.getPrefix() + "embed", "").split("\\|");
        if (args.length < 3) {
            MessageUtils.sendErrorMessage("Špatně napsaný příkaz! Př. `" + gw.getPrefix() + "embed <title> | <text> | #B0171F", channel);
            return;
        }
        Color c = null;
        String title = str[0].substring(1);
        String text = str[1].substring(1);
        String color = str[2].substring(1);

        try {
            if(Pattern.compile("#?([A-Fa-f\\d]){6}").matcher(color).find()) {
                c = Color.decode(color);
            } else {
                MessageUtils.sendErrorMessage("Špatně napsaný hex color! Př. `#B0171F`", channel);
                return;
            }
        }
        catch (NumberFormatException e){
            MessageUtils.sendErrorMessage("Špatně napsaný hex color! Př. `#B0171F`", channel);
            return;
        }
        channel.sendMessage(MessageUtils.getEmbed(c).setTitle(title).setDescription(text).build()).queue();

    }

    @Override
    public String getCommand() {
        return "embed";
    }

    @Override
    public String getDescription() {
        return "Tímto příkazem lze psát jako bot ve stylu embed.";
    }

    @Override
    public String getHelp() {
        return "%embed <title> | <text> | <HEX-CODE>";
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
}
