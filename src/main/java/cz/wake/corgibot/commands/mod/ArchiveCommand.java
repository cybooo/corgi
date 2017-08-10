package cz.wake.corgibot.commands.mod;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArchiveCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        try {
            long numposts = Long.valueOf(args[0]);
            TextChannel tx = member.getGuild().getTextChannelById(channel.getId());
            MessageHistory mh;

            if (!PermissionUtil.checkPermission(member, Permission.MESSAGE_HISTORY) || !PermissionUtil.checkPermission(member, Permission.MESSAGE_READ)) {
                MessageUtils.sendErrorMessage("Můžeš archivovat pouze channely do kterých vidíš!", channel);
                return;
            }
            if (!PermissionUtil.checkPermission(member.getGuild().getSelfMember(), Permission.MESSAGE_HISTORY)) {
                MessageUtils.sendErrorMessage("Nemám dostatečná práva k přečtení zpráv!", channel);
                return;
            }

            channel.sendTyping().submitAfter(5L, TimeUnit.SECONDS);

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
            MessageUtils.sendErrorMessage("Musíš zadat počet řádků!", channel);
        } catch (Exception e) {
            //
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
        return ".archive <počet-zpráv>";
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
    public CommandUse getUse() {
        return null;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
