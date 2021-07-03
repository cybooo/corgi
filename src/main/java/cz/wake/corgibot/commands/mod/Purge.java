package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CommandInfo(
        name = "purge",
        aliases = {"clean"},
        description = "Purge messages from bots, members or everyone.",
        help = "%purge <amount> - Deletes specified amount of messages.\n" +
                "%purge <amount> [@user|regex] - Deletes a specific number of messages for the selected user or text.",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL},
        botPerms = {Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY}
)
@SinceCorgi(version = "2.3.2")
public class Purge implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Number of rows not specified! I don't know how much to delete :(", channel);
            return;
        }

        try {
            int purge = Integer.parseInt(args[0]);
            if (purge <= 1) {
                MessageUtils.sendErrorMessage("Amount can't be lower than 1!", channel);
            } else if (purge > 100) {
                MessageUtils.sendErrorMessage("Amount can't be higher than 100!", channel);
            } else {
                message.delete().queue(useless -> {
                    message.getTextChannel().getHistory().retrievePast(purge).queue(msgsRaw -> {
                        List<Message> msgs = msgsRaw.stream().filter(mess -> !mess.getTimeCreated().plusWeeks(2).isBefore(OffsetDateTime.now())).collect(Collectors.toList());
                        message.getTextChannel().sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Deleting messages..").build()).queue(msg -> {
                            if (msgs.size() > 0) {
                                if (args.length >= 2) {
                                    String keyphrase = combineArgs(1, args);
                                    if (message.getMentionedUsers().size() == 0) {
                                        List<Message> keywordPurge = msgs.stream().filter(mes -> mes.getContentRaw().toLowerCase().contains(keyphrase.toLowerCase())).collect(Collectors.toList());
                                        if (keywordPurge.size() > 1)
                                            message.getTextChannel().deleteMessages(keywordPurge).queue();
                                        else if (keywordPurge.size() == 1)
                                            keywordPurge.get(0).delete().queue();
                                        if (keywordPurge.size() >= 1)
                                            msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Deleted " + keywordPurge.size() + " messages!").build()).queue();
                                        else
                                            msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Could not find messages to delete!").build()).queue();
                                    } else {
                                        List<String> mentions = new ArrayList<>();
                                        message.getMentionedUsers().forEach(user -> mentions.add(user.getAsMention()));
                                        Pattern p = Pattern.compile("([A-Z])+", Pattern.CASE_INSENSITIVE);
                                        Matcher m = p.matcher(keyphrase);
                                        if (mentions.stream().anyMatch(mentions::contains) && m.find()) {
                                            msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | You can't delete mentions and selected text at once!").build()).queue();
                                        } else {
                                            List<Message> mentionPurge = msgs.stream().filter(mes -> message.getMentionedUsers().stream().anyMatch(mes.getAuthor()::equals)).collect(Collectors.toList());
                                            if (mentionPurge.size() > 1)
                                                message.getTextChannel().deleteMessages(mentionPurge).queue();
                                            else if (mentionPurge.size() == 1)
                                                mentionPurge.get(0).delete().queue();
                                            if (mentionPurge.size() >= 1)
                                                msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Succesfully deleted " + mentionPurge.size() + " message(s) from: `" + StringUtils.join(message.getMentionedUsers().stream().map(user -> user.getName() + "#" + user.getDiscriminator()).collect(Collectors.toList()), ", ") + "`").build()).queue();
                                            else
                                                msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Could not find any message from specified users!").build()).queue();
                                        }
                                    }
                                } else {
                                    if (msgs.size() == 1)
                                        msgs.get(0).delete().queue(delet -> msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Succesfully deleted " + msgs.size() + " messages.").build()).queue());
                                    else
                                        message.getTextChannel().deleteMessages(msgs).queue(delet -> msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Succesfully deleted " + msgs.size() + " messages.").build()).queue());
                                }
                            } else
                                msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | You can't delete messages older than 14 days!").build()).queue();
                        });
                    });
                });
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendErrorMessage("Invalid time format!", channel);
        }
    }

    public String combineArgs(String[] args) {
        return combineArgs(0, args.length, args);
    }

    public String combineArgs(int start, String[] args) {
        return combineArgs(start, args.length, args);
    }

    public String combineArgs(int start, int end, String[] args) {
        if (end > args.length)
            throw new IllegalArgumentException("End value specified is longer than the arguments provided.");
        return StringUtils.join(Arrays.copyOfRange(args, start, end), " ");
    }
}
