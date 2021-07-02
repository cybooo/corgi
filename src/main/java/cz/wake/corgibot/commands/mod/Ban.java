package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
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

@SinceCorgi(version = "0.7")
public class Ban implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            MessageUtils.sendErrorMessage("I can't ban members! Give me the `BAN_MEMBERS` or `ADMINISTRATOR` permission!", channel);
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("You need to mention someone! Example: `" + gw.getPrefix() + "ban @User`", channel);
            return;
        }
        if (message.getMentionedUsers().size() > 20) {
            MessageUtils.sendErrorMessage("You can only ban 20 members at once!", channel);
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
                        .append(" can't be banned, because he was not found in this server!");
            } else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to ban ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to ban ")
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
                message.getGuild().ban(m, 1).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | Succesfully banned ")
                            .append(m.getAsMention());
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | Could not ban ")
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
        return "Ban user(s) from this server";
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
