package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.LinkedList;

@SinceCorgi(version = "0.7")
public class Ban implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            MessageUtils.sendErrorMessage("Nemám dostatečná práva na vyhazování uživatelů! Přidej mi právo na `BAN_MEMBERS` nebo `ADMINISTRATOR`", channel);
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš nejdříve někoho označit! Př. `" + gw.getPrefix() + "ban @User`", channel);
            return;
        }
        if (message.getMentionedUsers().size() > 20) {
            MessageUtils.sendErrorMessage("Maximální počet uživatelů, kterých lze najednou zabanovat je 20!", channel);
            return;
        }

        StringBuilder builder = new StringBuilder();
        LinkedList<Member> members = new LinkedList<>();
        message.getMentionedUsers().stream().forEach((u) -> {
            Member m = message.getGuild().getMember(u);
            if (m == null) {
                builder.append("\n")
                        .append(EmoteList.WARNING)
                        .append(" | ")
                        .append(u.getAsMention())
                        .append(" nemůže být zabanován, jelikož není evidován na serveru!");
            } else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | Nemáš dostatečná práva na zabanování ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | Nemáš dostatečná práva na zabanování ")
                        .append(FormatUtil.formatUser(u));
            } else
                members.add(m);
        });
        if (members.isEmpty())
            MessageUtils.sendErrorMessage(builder.toString(), channel);
        else {
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                boolean last = i + 1 == members.size();
                message.getGuild().getController().ban(m, 1).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | Uspěšně zabanován ")
                            .append(m.getAsMention());
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | Nepodařilo se zabanovat ")
                            .append(FormatUtil.formatUser(m.getUser()));
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                });
            }
        }
    }

    @Override
    public String getCommand() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Banování uživatelů na serveru.";
    }

    @Override
    public String getHelp() {
        return "%ban @user [@user]";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.BAN_MEMBERS};
    }
}
