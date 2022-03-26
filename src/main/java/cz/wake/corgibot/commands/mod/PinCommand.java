package cz.wake.corgibot.commands.mod;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.PermissionUtil;

@CommandMarker
public class PinCommand extends ApplicationCommand {

    @JDASlashCommand(
            name = "pin",
            description = "Command to generate a message to pin"
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "message-to-pin", description = "Message that should be pinned.") String message) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        if (!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE)) {
            event.reply("I can't pin messages! Give me the `MANAGE_CHANNEL`, `MESSAGE_MANAGE` or `ADMINISTRATOR` permission!" ).queue();
            return;
        }
        Message pinnedMessage = event.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle(event.getUser().getName(), null)
                .setThumbnail(MessageUtils.getAvatar(event.getUser())).setDescription(message)
                .build()).complete();
        pinnedMessage.pin().complete();
    }
    
}
