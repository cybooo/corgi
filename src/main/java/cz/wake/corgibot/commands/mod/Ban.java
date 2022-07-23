package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.LinkedList;

@CommandInfo(
        name = "ban",
        help = "%ban @user [@user]",
        description = "Ban user(s) from this server",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.BAN_MEMBERS}
)
@SinceCorgi(version = "0.7")
public class Ban implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.ban.cant-ban-members"), channel);
            return;
        }
        if (message.getMentions().getMentions().isEmpty()) {
            MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.ban.need-to-mention"), gw.getPrefix()), channel);
            return;
        }
        if (message.getMentions().getMentions().size() > 20) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.ban.only-20-members"), channel);
            return;
        }

        StringBuilder builder = new StringBuilder();
        LinkedList<Member> members = new LinkedList<>();
        message.getMentions().getUsers().forEach((u) -> {
            Member m = message.getGuild().getMember(u);
            if (m == null) {
                builder.append("\n")
                        .append(EmoteList.WARNING)
                        .append(" | ")
                        .append(String.format(I18n.getLoc(gw, "commands.ban.cant-be-banned"), u.getAsMention()));
            } else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | ")
                        .append(String.format(I18n.getLoc(gw, "commands.ban.not-enough-permissions-to-ban"), FormatUtil.formatUser(u)));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | ")
                        .append(String.format(I18n.getLoc(gw, "commands.ban.not-enough-permissions-to-ban"), FormatUtil.formatUser(u)));

            } else
                members.add(m);
        });
        if (members.isEmpty())
            MessageUtils.sendErrorMessage(builder.toString(), channel);
        else {
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                boolean last = i + 1 == members.size();
                message.getGuild().ban(m, 1).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | ")
                            .append(String.format(I18n.getLoc(gw, "command.ban.successfully-banned"), m.getAsMention()));
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | ")
                            .append(String.format(I18n.getLoc(gw, "commands.ban.could-not-ban"), FormatUtil.formatUser(m.getUser())));
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                });
            }
        }
    }

}
