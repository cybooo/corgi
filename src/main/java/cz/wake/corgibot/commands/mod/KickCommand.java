package cz.wake.corgibot.commands.mod;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.FormatUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.internal.utils.PermissionUtil;

@CommandMarker
@SinceCorgi(version = "0.7")
public class KickCommand extends ApplicationCommand{

    @JDASlashCommand(
            name = "kick",
            description = "Kick user(s) from this server"
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "user-to-kick", description = "User to kick.") Member m) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.BAN_MEMBERS)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.KICK_MEMBERS)) {
            event.reply("I can't kick members! Give me the `KICK_MEMBERS` or `ADMINISTRATOR` permission!" ).queue();
            return;
        }

        if (!PermissionUtil.canInteract(event.getMember(), m)) {
            String message = "\n" +
                    EmoteList.RED_DENY +
                    " | You don't have enough permissions to kick " +
                    FormatUtil.formatUser(m.getUser());
            event.reply(message).queue();
        } else if (!PermissionUtil.canInteract(event.getGuild().getSelfMember(), m)) {
            String message = "\n" +
                    EmoteList.RED_DENY +
                    " | You don't have enough permissions to kick " +
                    FormatUtil.formatUser(m.getUser());
            event.reply(message).queue();
        } else {
            event.getGuild().kick(m).queue((v) -> {
                String builder = "\n" +
                        EmoteList.GREEN_OK +
                        " | Succesfully kicked " +
                        m.getAsMention();
                event.reply(builder).queue();

            }, (t) -> {
                String builder = "\n" +
                        EmoteList.RED_DENY +
                        " | Could not kick " +
                        FormatUtil.formatUser(m.getUser());
                event.reply(builder).queue();

            });
        }

    }

}
