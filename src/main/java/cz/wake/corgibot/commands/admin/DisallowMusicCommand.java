package cz.wake.corgibot.commands.admin;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.PermissionUtil;

@CommandMarker
@SinceCorgi(version = "1.3.5")
public class DisallowMusicCommand extends ApplicationCommand {

    @JDASlashCommand(
            name = "disallowmusic",
            description = "Disallows a music command for a specified role, if it was allowed."
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "command_type") String commandType,
                        @AppOption(name = "role_id") long roleId) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL)) {
            return;
        }
        if (commandType.equals("play") || commandType.equals("nowplaying") || commandType.equals("skip") ||
                commandType.equals("stop") || commandType.equals("volume")) {
            event.reply("If the **" + commandType + "** command was allowed, it's now disallowed!\n" +
                    "**Note:** This command only removes an already allowed command!\n" +
                    "If you want to disallow a command for some role, you need to directly allow the commands for each role you want.")
                    .setEphemeral(true).queue();
            CorgiBot.getInstance().getSql().deleteRoleMusicCommand(event.getGuild().getId(), Long.toString(roleId), commandType);
        } else {
            event.reply("You need to specify a music command: **play** / **nowplaying** / **skip** / **stop** / **volume**!")
                    .setEphemeral(true).queue();
        }
    }

}
