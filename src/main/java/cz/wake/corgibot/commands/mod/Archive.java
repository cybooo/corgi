package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandInfo(
        name = "archive",
        help = "commands.archive.help",
        description = "commands.archive.description",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "1.0")
public class Archive implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            if (!PermissionUtil.checkPermission(member, Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(member, Permission.VIEW_CHANNEL)) {
                MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.archive.archive-only-see"), 10000, channel);
                return;
            }
            if (!PermissionUtil.checkPermission(member.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.archive.no-permissions"), 20000, channel);
                return;
            }

            long numposts = Long.parseLong(args[0]);

            if (numposts > 100) {
                MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.archive.only-100-messages"), 20000, channel);
                return;
            }

            MessageEmbed logMess = MessageUtils.getEmbed(Constants.GREEN).setDescription(I18n.getLoc(gw, "commands.archive.generating-log")).build();
            channel.sendMessageEmbeds(logMess).queue();

            TextChannel tx = member.getGuild().getTextChannelById(channel.getId());
            MessageHistory mh;

            mh = new MessageHistory(channel);

            RestAction<List<Message>> messages = mh.retrievePast((int) numposts);
            StringBuilder builder = new StringBuilder(String.format(I18n.getLoc(gw, "commands.archive.channel-archive"), tx.getName()));
            for (int i = messages.complete().size() - 1; i >= 0; i--) {
                Message m = messages.complete().get(i);
                builder.append("[").append(m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
                builder.append(m.getAuthor().getName()).append(" : ");
                builder.append(m.getContentRaw()).append(m.getAttachments().size() > 0 ? " " + m.getAttachments().get(0).getUrl() : "").append("\n");
            }

            MessageEmbed mess = MessageUtils.getEmbed(Constants.GREEN).setTitle(I18n.getLoc(gw, I18n.getLoc(gw, "commands.archive.generated-log"))).setDescription(String.format(I18n.getLoc(gw, "commands.archive.sending-log"), numposts, MessageUtils.hastebin(builder.toString()))).build();
            channel.sendMessageEmbeds(mess).queue();
        } catch (ArrayIndexOutOfBoundsException ax) {
            MessageUtils.sendAutoDeletedMessage(String.format(I18n.getLoc(gw, "commands.archive.provide-amount-lines"), gw.getPrefix()), 20000, channel);
        } catch (Exception e) {
            CorgiBot.LOGGER.error(I18n.getLoc(gw, "internal.error.command-failed"), e);
        }

    }

}
