package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandInfo(
        name = "archive",
        help = "%archive <amount-of-messages>",
        description = "Archive messages and upload them to hastebin.",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "1.0")
public class Archive implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            if (!PermissionUtil.checkPermission(member, Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(member, Permission.MESSAGE_READ)) {
                MessageUtils.sendAutoDeletedMessage("You can only archive commands you can view!", 10000, channel);
                return;
            }
            if (!PermissionUtil.checkPermission(member.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendAutoDeletedMessage("I'm missing the `MESSAGE_HISTORY` permission!", 20000, channel);
                return;
            }

            long numposts = Long.parseLong(args[0]);

            if (numposts > 100) {
                MessageUtils.sendAutoDeletedMessage("Unable to generate log with more than 100 messages due to Discord API limitations", 20000, channel);
                return;
            }

            MessageEmbed logMess = MessageUtils.getEmbed(Constants.GREEN).setDescription("Generating log, please wait!").build();
            channel.sendMessageEmbeds(logMess).queue();

            TextChannel tx = member.getGuild().getTextChannelById(channel.getId());
            MessageHistory mh;

            mh = new MessageHistory(channel);

            RestAction<List<Message>> messages = mh.retrievePast((int) numposts);
            StringBuilder builder = new StringBuilder("-- Channel archive: [" + tx.getName() + "] --\n\n");
            for (int i = messages.complete().size() - 1; i >= 0; i--) {
                Message m = messages.complete().get(i);
                builder.append("[").append(m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
                builder.append(m.getAuthor().getName()).append(" : ");
                builder.append(m.getContentRaw()).append(m.getAttachments().size() > 0 ? " " + m.getAttachments().get(0).getUrl() : "").append("\n");
            }

            MessageEmbed mess = MessageUtils.getEmbed(Constants.GREEN).setTitle("Generated log file").setDescription("Sending the generated log file with " + numposts + " messages.\n" +
                    "**Odkaz**: " + MessageUtils.hastebin(builder.toString())).build();
            channel.sendMessageEmbeds(mess).queue();
        } catch (ArrayIndexOutOfBoundsException ax) {
            MessageUtils.sendAutoDeletedMessage("You need to provide the amount of lines! Example: `" + gw.getPrefix() + "archive 10`", 20000, channel);
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Something went wrong when archiving!", e);
        }

    }

}
