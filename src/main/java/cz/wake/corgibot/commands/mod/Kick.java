package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.LinkedList;

public class Kick implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if (!PermissionUtil.checkPermission(message.getGuild().getSelfMember(), Permission.KICK_MEMBERS)){
            MessageUtils.sendErrorMessage("Nemám dostatečná práva na vyhazování uživatelů! Přidej mi právo na `KICK_MEMBERS` nebo `ADMINISTRATOR`", channel);
            return;
        }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš nejdříve někoho označit!", channel);
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
                        .append(" nemůže být vyhozen, jelikož není evidován na serveru!");
            }  else if (!PermissionUtil.canInteract(message.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | Nemáš dostatečná práva na vyhození ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(message.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | Nemáš dostatečná práva na vyhození ")
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
                message.getGuild().getController().kick(m).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | Uspěšně vykopnut ")
                            .append(m.getAsMention());
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | Nepodařilo se vyhodit ")
                            .append(FormatUtil.formatUser(m.getUser()));
                    if (last)
                        MessageUtils.sendErrorMessage(builder.toString(), channel);
                });
            }
        }
    }

    @Override
    public String getCommand() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Vyhození uživatele(ů) z serveru.";
    }

    @Override
    public String getHelp() {
        return "%kick @user [@user]";
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
