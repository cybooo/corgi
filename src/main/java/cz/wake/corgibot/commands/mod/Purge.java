package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
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
        description = "commands.purge.description",
        help = "commands.purge.help",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL},
        botPerms = {Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY}
)
@SinceCorgi(version = "2.3.2")
public class Purge implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.purge.rows-not-specified"), channel);
            return;
        }

        try {
            int purge = Integer.parseInt(args[0]);
            if (purge <= 1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.purge.rows-lower-than-0"), channel);
            } else if (purge > 100) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.purge.rows-higher-than-100"), channel);
            } else {
                message.delete().queue(useless -> message.getTextChannel().getHistory().retrievePast(purge).queue(msgsRaw -> {
                    List<Message> msgs = msgsRaw.stream().filter(mess -> !mess.getTimeCreated().plusWeeks(2).isBefore(OffsetDateTime.now())).collect(Collectors.toList());
                    message.getTextChannel().sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setDescription(I18n.getLoc(gw, "commands.purge.deleting-messages")).build()).queue(msg -> {
                        if (msgs.size() > 0) {
                            if (args.length >= 2) {
                                String keyphrase = combineArgs(1, args);
                                if (message.getMentions().getMentions().size() == 0) {
                                    List<Message> keywordPurge = msgs.stream().filter(mes -> mes.getContentRaw().toLowerCase().contains(keyphrase.toLowerCase())).collect(Collectors.toList());
                                    if (keywordPurge.size() > 1)
                                        message.getTextChannel().deleteMessages(keywordPurge).queue();
                                    else if (keywordPurge.size() == 1)
                                        keywordPurge.get(0).delete().queue();
                                    if (keywordPurge.size() >= 1)
                                        msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + String.format(I18n.getLoc(gw, "commands.purge.deleted-messages"), keywordPurge.size())).build()).queue();
                                    else
                                        msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | " + I18n.getLoc(gw, "commands.purge.no-messages-found")).build()).queue();
                                } else {
                                    List<String> mentions = new ArrayList<>();
                                    message.getMentions().getUsers().forEach(user -> mentions.add(user.getAsMention()));
                                    Pattern p = Pattern.compile("([A-Z])+", Pattern.CASE_INSENSITIVE);
                                    Matcher m = p.matcher(keyphrase);
                                    if (mentions.stream().anyMatch(mentions::contains) && m.find()) {
                                        msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | " + I18n.getLoc(gw, "commands.purge.no-messages-mentions")).build()).queue();
                                    } else {
                                        List<Message> mentionPurge = msgs.stream().filter(mes -> message.getMentions().getUsers().stream().anyMatch(mes.getAuthor()::equals)).collect(Collectors.toList());
                                        if (mentionPurge.size() > 1)
                                            message.getTextChannel().deleteMessages(mentionPurge).queue();
                                        else if (mentionPurge.size() == 1)
                                            mentionPurge.get(0).delete().queue();
                                        if (mentionPurge.size() >= 1)
                                            msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | " + String.format(I18n.getLoc(gw, "commands.purge.deleted-messages-from"), mentionPurge.size(), StringUtils.join(message.getMentions().getUsers().stream().map(user -> user.getName() + "#" + user.getDiscriminator()).collect(Collectors.toList()), ", "))).build()).queue();
                                        else
                                            msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | " + I18n.getLoc(gw, "commands.purge.not-found-from-users")).build()).queue();
                                    }
                                }
                            } else {
                                if (msgs.size() == 1)
                                    msgs.get(0).delete().queue(delet -> msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | " + String.format(I18n.getLoc(gw, "commands.purge.deleted-messages"), msgs.size())).build()).queue());
                                else
                                    message.getTextChannel().deleteMessages(msgs).queue(delet -> msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | " + String.format(I18n.getLoc(gw, "commands.purge.deleted-messages"), msgs.size())).build()).queue());
                            }
                        } else
                            msg.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | " + I18n.getLoc(gw, "commands.purge.cant-delete-14-days")).build()).queue();
                    });
                }));
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.purge.invalid-time"), channel);
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
