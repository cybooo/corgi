package cz.wake.corgibot.commands.mod;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

@CommandMarker
@SinceCorgi(version = "1.0")
public class ArchiveCommand extends ApplicationCommand {

    @JDASlashCommand(
            name = "archive",
            description = "Archive messages and upload them to hastebin."
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "messages", description = "Amount of messages to archive") long amount) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(event.getMember(), Permission.VIEW_CHANNEL)) {
            event.reply("You can only archive commands you can view!").queue();
            return;
        }
        try {
            if (amount > 100) {
                event.reply("Unable to generate log with more than 100 messages due to Discord API limitations").queue();
                return;
            }

            MessageEmbed logMess = MessageUtils.getEmbed(Constants.GREEN).setDescription("Generating log, please wait!").build();
            event.getChannel().sendMessageEmbeds(logMess).queue();

            TextChannel tx = event.getGuild().getTextChannelById(event.getChannel().getId());
            MessageHistory mh;

            mh = new MessageHistory(event.getChannel());

            RestAction<List<Message>> messages = mh.retrievePast((int) amount);
            StringBuilder builder = new StringBuilder("-- Channel archive: [" + tx.getName() + "] --\n\n");
            for (int i = messages.complete().size() - 1; i >= 0; i--) {
                Message m = messages.complete().get(i);
                builder.append("[").append(m.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
                builder.append(m.getAuthor().getName()).append(" : ");
                builder.append(m.getContentRaw()).append(m.getAttachments().size() > 0 ? " " + m.getAttachments().get(0).getUrl() : "").append("\n");
            }

            MessageEmbed mess = MessageUtils.getEmbed(Constants.GREEN).setTitle("Generated log file").setDescription("Sending the generated log file with " + amount + " messages.\n" +
                    "**Odkaz**: " + MessageUtils.hastebin(builder.toString())).build();
            event.replyEmbeds(mess).queue();
        } catch (ArrayIndexOutOfBoundsException ax) {
            event.reply("You need to provide the amount of lines! Example: `/archive 10`").queue();
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Something went wrong while archiving!", e);
        }
    }

}
