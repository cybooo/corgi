package cz.wake.corgibot.commands.mod;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.application.slash.annotations.VarArgs;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

@CommandMarker
@SinceCorgi(version = "0.7")
public class BanCommand extends ApplicationCommand{

    @JDASlashCommand(
            name = "ban",
            description = "Ban user(s) from this server"
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "users-to-ban", description = "Users to ban.") @VarArgs(25) List<User> userList) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.BAN_MEMBERS)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.BAN_MEMBERS)) {
            event.reply("I can't ban members! Give me the `BAN_MEMBERS` or `ADMINISTRATOR` permission!" ).queue();
            return;
        }

        StringBuilder builder = new StringBuilder();
        LinkedList<Member> members = new LinkedList<>();
        userList.forEach((u) -> {
            Member m = event.getGuild().getMember(u);
            if (m == null) {
                builder.append("\n")
                        .append(EmoteList.WARNING)
                        .append(" | ")
                        .append(u.getAsMention())
                        .append(" can't be banned, because he was not found in this server!");
            } else if (!PermissionUtil.canInteract(event.getMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to ban ")
                        .append(FormatUtil.formatUser(u));
            } else if (!PermissionUtil.canInteract(event.getGuild().getSelfMember(), m)) {
                builder.append("\n")
                        .append(EmoteList.RED_DENY)
                        .append(" | You don't have enough permissions to ban ")
                        .append(FormatUtil.formatUser(u));
            } else
                members.add(m);
        });
        if (members.isEmpty())
            event.replyEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(builder.toString())
                    .build()).queue();
        else {
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                boolean last = i + 1 == userList.size();
                event.getGuild().ban(m, 1).queue((v) -> {
                    builder.append("\n")
                            .append(EmoteList.GREEN_OK)
                            .append(" | Succesfully banned ")
                            .append(m.getAsMention());
                    if (last)
                        event.replyEmbeds(new EmbedBuilder()
                                .setColor(Constants.BLUE)
                                .setDescription(builder.toString())
                                .build()).queue();
                    }, (t) -> {
                    builder.append("\n")
                            .append(EmoteList.RED_DENY)
                            .append(" | Could not ban ")
                            .append(FormatUtil.formatUser(m.getUser()));
                    if (last)
                        event.replyEmbeds(new EmbedBuilder()
                                .setColor(Constants.RED)
                                .setDescription(builder.toString())
                                .build()).queue();
                });
            }
        }

    }

}
