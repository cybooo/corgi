package cz.wake.corgibot.commands.admin;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.annotations.SinceCorgi;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.PermissionUtil;

@CommandMarker
@SinceCorgi(version = "0.8")
public class SayCommand extends ApplicationCommand {

    @JDASlashCommand(
            name = "say",
            description = "This command can be used to write as the bot."
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "text", description = "Text the bot should say.") String text) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        event.getChannel().sendMessage(text).queue();
    }

}
