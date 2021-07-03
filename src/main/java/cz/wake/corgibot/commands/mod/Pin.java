package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "pin",
        description = "Command to pin a message, or a generate message to pin",
        help = "%pin <ID|message>` - Pins a message by ID, or generates a new one to pin",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE},
        botPerms = {Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE}
)
public class Pin implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 1 && args[0].matches("[0-9]{18,22}")) {

            Message msg = channel.retrieveMessageById(args[0].trim()).complete();
            if (msg == null) {
                MessageUtils.sendErrorMessage("Message not found!", channel);
                return;
            }
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else if (args.length != 0) {
            Message msg = channel.sendMessage(new EmbedBuilder().setTitle(member.getUser().getName(), null)
                    .setThumbnail(MessageUtils.getAvatar(member.getUser())).setDescription(MessageUtils.getMessage(args, 0))
                    .build()).complete();
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Help for command %ping".replace("%", gw.getPrefix()))
                    .setDescription(getDescription()).build()).queue();
        }
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
