package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.LinkedList;

@CommandInfo(
        name = "kick",
        help = "%kick @user [@user]",
        description = "Kick user(s) from this server",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.KICK_MEMBERS}
)
@SinceCorgi(version = "0.7")
public class Kick implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.KICK_MEMBERS)) {
            MessageUtils.sendErrorMessage("Nemám dostatečná práva na vyhazování uživatelů! Přidej mi právo na `KICK_MEMBERS` nebo `ADMINISTRATOR`", channel);
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš nejdříve někoho označit! Př. `" + gw.getPrefix() + "kick @User`", channel);
            return;
        }
        if (message.getMentionedUsers().size() > 20) {
            MessageUtils.sendErrorMessage("Maximální počet uživatelů, kterých lze najednou vyhodit je 20!", channel);
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
                        .append(" can't be kicked, because he was not found in this server!");
            } else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to kick ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to kick ")
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
                message.getGuild().kick(m).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | Succesfully kicked ")
                            .append(m.getAsMention());
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | Could not kick ")
                            .append(FormatUtil.formatUser(m.getUser()));
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                });
            }
        }
    }

}
