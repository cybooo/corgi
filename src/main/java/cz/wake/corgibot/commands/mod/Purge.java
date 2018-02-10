package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SinceCorgi(version = "1.3.0")
public class Purge implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length >= 1) {
            User targetUser = null;
            int amount;
            if (args.length == 1 && args[0].matches("\\d+")) {
                amount = getInt(args[0], -1);
            } else if (args.length == 2 && args[1].matches("\\d+")) {
                amount = getInt(args[1], -1);
                try {
                    String id = args[0].replaceAll("[^0-9]", "");
                    targetUser = CorgiBot.getJda().getUserById(id);
                } catch (ErrorResponseException e) {
                    MessageUtils.sendErrorMessage("Požadovaný uživatel nemůže být nalezen, zkus ho označit pomocí @ nebo napiš jeho ID.", channel);
                    return;
                }
            } else {
                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu purge").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
                return;
            }

            if (amount < 1) {
                MessageUtils.sendErrorMessage("Nemohu mazat méně jak 1 zprávu. Zkus to znova...", channel);
                return;
            }

            if (!member.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendErrorMessage("Nemám dostatečná práva na správu zpráv a čtení historie. Nemohu tedy mazat zprávy...", channel);
                return;
            }

            MessageHistory history = new MessageHistory(channel);
            TextChannel textChannel = (TextChannel) channel;
            int toRetrieve = amount;
            int i = 0;
            message.delete().complete();
            outer:
            while (toRetrieve > 0) {
                if (history.retrievePast((targetUser == null ? Math.min(toRetrieve, 100) : 100)).complete().isEmpty()) {
                    break;
                }

                List<Message> toDelete = new ArrayList<>();
                for (Message msg : history.getRetrievedHistory()) {
                    if (msg.getCreationTime().plusWeeks(2).isBefore(OffsetDateTime.now())) break outer;
                    if (msg.getId().equals(message.getId())) continue;
                    if ((targetUser != null && msg.getAuthor().getId().equals(targetUser.getId())) || targetUser == null) {
                        toDelete.add(msg);
                        i++;
                        toRetrieve--;
                    }
                    if (toRetrieve == 0) break;
                }
                try {
                    if (toDelete.size() == 0) break;
                    if (toDelete.size() == 1) {
                        channel.deleteMessageById(toDelete.get(0).getId()).complete();
                        break;
                    }
                    textChannel.deleteMessages(toDelete).complete();
                } catch (PermissionException e) {
                    MessageUtils.sendErrorMessage("Nemám dostatečná práva na provedení této akce! Zřejmě mi chybí právo na mazání zpráv!", channel);
                    return;
                } catch (ErrorResponseException e) {
                    MessageUtils.sendErrorMessage("Interní chyba při provádění akce!", channel);
                    return;
                }
                toDelete.clear();
            }
            if (i > 0) {
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                                .setDescription(String.format(EmoteList.GREEN_OK + " | Smazáno `%s` zpráv!", i)).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Nemohu najít zprávy, které mám smazat!", channel);
            }
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu purge").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
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
                "%purge <@uživatel> <počet> - Smaže konkrétné počet zpráv pro zvoleného uživatele.";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"clean"};
    }

    public static int getInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
