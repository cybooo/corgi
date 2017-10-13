package cz.wake.corgibot.commands.mod;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.LinkedList;

public class Ban implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.BAN_MEMBERS)){
            MessageUtils.sendErrorMessage("Nemám dostatečná práva na vyhazování uživatelů! Přidej mi právo na `BAN_MEMBERS` nebo `ADMINISTRATOR`", channel);
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš nejdříve někoho označit!", channel);
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
                        .append(":warning:")
                        .append(" | ")
                        .append(u.getAsMention())
                        .append(" nemůže být zabanován, jelikož není evidován na serveru!");
            }  else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(":error:")
                        .append(" | Nemáš dostatečná práva na zabanování ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(":error:")
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
                            .append(":success:")
                            .append(" | Uspěšně zabanován ")
                            .append(m.getAsMention());
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(":error:")
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
        return ".ban @user [@user]";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
