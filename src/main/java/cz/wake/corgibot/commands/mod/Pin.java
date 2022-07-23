package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "pin",
        description = "commands.pin.description",
        help = "commands.pin.help",
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
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.pin.message-not-found"), channel);
                return;
            }
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else if (args.length != 0) {
            Message msg = channel.sendMessageEmbeds(new EmbedBuilder().setTitle(member.getUser().getName(), null)
                    .setThumbnail(MessageUtils.getAvatar(member.getUser())).setDescription(MessageUtils.getMessage(args, 0))
                    .build()).complete();
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setTitle(I18n.getLoc(gw, "commands.pin.embed-title").replace("%", gw.getPrefix()))
                    .setDescription(getDescription()).build()).queue();
        }
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
