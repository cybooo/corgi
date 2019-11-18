package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SinceCorgi(version = "2.3.2")
public class Purge implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Nezadaný počet řádků! Nevím kolik toho mám smazat :(", channel);
            return;
        }

        try {
            int purge = Integer.parseInt(args[0]);
            if (purge <= 1) {
                MessageUtils.sendErrorMessage("Mazat méně jak 1 nelze!", channel);
            } else if (purge > 100) {
                MessageUtils.sendErrorMessage("Mazat méně jak 100 nelze!", channel);
            } else {
                message.delete().queue(useless -> {
                    message.getTextChannel().getHistory().retrievePast(purge).queue(msgsRaw -> {
                        List<Message> msgs = msgsRaw.stream().filter(mess -> !mess.getCreationTime().plusWeeks(2).isBefore(OffsetDateTime.now())).collect(Collectors.toList());
                        message.getTextChannel().sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Mažu zpravy...").build()).queue(msg -> {
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
                                            msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Smazáno " + keywordPurge.size() + " zpráv!").build()).queue();
                                        else
                                            msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Nepdařilo se najít požadované zprávy k smazání!").build()).queue();
                                    } else {
                                        List<String> mentions = new ArrayList<>();
                                        message.getMentionedUsers().forEach(user -> mentions.add(user.getAsMention()));
                                        Pattern p = Pattern.compile("([A-Z])+", Pattern.CASE_INSENSITIVE);
                                        Matcher m = p.matcher(keyphrase);
                                        if (mentions.stream().anyMatch(mentions::contains) && m.find()) {
                                            msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Nelze mazat označení a zvolený text zároveň!").build()).queue();
                                        } else {
                                            List<Message> mentionPurge = msgs.stream().filter(mes -> message.getMentionedUsers().stream().anyMatch(mes.getAuthor()::equals)).collect(Collectors.toList());
                                            if (mentionPurge.size() > 1)
                                                message.getTextChannel().deleteMessages(mentionPurge).queue();
                                            else if (mentionPurge.size() == 1)
                                                mentionPurge.get(0).delete().queue();
                                            if (mentionPurge.size() >= 1)
                                                msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Úspěšně smazáno " + mentionPurge.size() + " zpráv(y) od: `" + StringUtils.join(message.getMentionedUsers().stream().map(user -> user.getName() + "#" + user.getDiscriminator()).collect(Collectors.toList()), ", ") + "`").build()).queue();
                                            else
                                                msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Nepodařilo se najít žádnou zprávu od označených uživatelů.").build()).queue();
                                        }
                                    }
                                } else {
                                    if (msgs.size() == 1)
                                        msgs.get(0).delete().queue(delet -> msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Úspěšně smazáno " + msgs.size() + " zpráv.").build()).queue());
                                    else
                                        message.getTextChannel().deleteMessages(msgs).queue(delet -> msg.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | Úspěšně smazáno " + msgs.size() + " zpráv.").build()).queue());
                                }
                            } else
                                msg.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription(EmoteList.RED_DENY + " | Nelze mazat zprávy starší než 14 dní!").build()).queue();
                        });
                    });
                });
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendErrorMessage("Chyba! Špatně zadaný formát čísla!", channel);
        }
    }


    @Override
    public String getCommand() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "Mazání zpráv botů, uživatelů a nebo všech.";
    }

    @Override
    public String getHelp() {
        return "%purge <počet> - Smaže požadovaný počet zpráv.\n" +
                "%purge <počet> [@uživatel|regex]- Smaže konkrétné počet zpráv pro zvoleného uživatele nebo text.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    public Permission[] botPermission() {
        return new Permission[]{Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"clean"};
    }

    public String combineArgs(String[] args) {
        return combineArgs(0, args.length, args);
    }

    public String combineArgs(int start, String[] args) {
        return combineArgs(start, args.length, args);
    }

    public String combineArgs(int start, int end, String[] args) {
        if (end > args.length) throw new IllegalArgumentException("End value specified is longer than the arguments provided.");
        return StringUtils.join(Arrays.copyOfRange(args, start, end), " ");
    }
}
