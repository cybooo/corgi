package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Archive implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        try {
            if (!PermissionUtil.checkPermission(member, Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(member, Permission.MESSAGE_READ)) {
                MessageUtils.sendAutoDeletedMessage("Můžeš archivovat pouze channely do kterých vidíš!", 10000, channel);
                return;
            }
            if (!PermissionUtil.checkPermission(member.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendAutoDeletedMessage("Nemám dostatečná práva k přečtení zpráv! Právo: `MESSAGE_HISTORY`", 20000, channel);
                return;
            }

            long numposts = Long.valueOf(args[0]);

            TextChannel tx = member.getGuild().getTextChannelById(channel.getId());
            MessageHistory mh;

            mh = new MessageHistory(channel);

            RestAction<List<Message>> messages = mh.retrievePast((int) numposts);
            StringBuilder builder = new StringBuilder("-- Archiv kanálu: [" + tx.getName() + "] --\n\n");
            for (int i = messages.complete().size() - 1; i >= 0; i--) {
                Message m = messages.complete().get(i);
                builder.append("[").append(m.getCreationTime() == null ? "NEZNÁMÝ ČAS" : m.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
                builder.append(m.getAuthor() == null ? "????" : m.getAuthor().getName()).append(" : ");
                builder.append(m.getContent()).append(m.getAttachments() != null && m.getAttachments().size() > 0 ? " " + m.getAttachments().get(0).getUrl() : "").append("\n");
            }

            MessageEmbed mess = MessageUtils.getEmbed(Constants.GREEN).setTitle("Vygenerovaný log soubor").setDescription("Zasílám vygenerovaný log soubor s " + numposts + " zprávami.\n" +
                    "**Odkaz**: " + MessageUtils.hastebin(builder.toString())).build();
            channel.sendMessage(mess).queue();
        } catch (ArrayIndexOutOfBoundsException ax) {
            MessageUtils.sendAutoDeletedMessage("Musíš zadat počet řádků! Př. `" + guildPrefix + "archive 10`", 20000, channel);
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Chyba při provádení příkazu .archive !", e);
        }

    }

    @Override
    public String getCommand() {
        return "archive";
    }

    @Override
    public String getDescription() {
        return "Archivování zpráv na HasteBin.";
    }

    @Override
    public String getHelp() {
        return "%archive <počet-zpráv>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"log"};
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
