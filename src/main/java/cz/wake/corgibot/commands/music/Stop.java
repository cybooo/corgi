package cz.wake.corgibot.commands.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.music.AudioEngine;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.ArrayList;

@CommandInfo(
        name = "stop",
        aliases = {"stopsong"},
        description = "commands.music-stop.description",
        help = "commands.music-stop.help",
        category = CommandCategory.MUSIC
)
@SinceCorgi(version = "1.3.5")
public class Stop implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        boolean canUse = false;
        ArrayList<String> roles = CorgiBot.getInstance().getSql().getRoleMusicRoles(message.getGuild().getId(), getCommand());
        if (member.getRoles().isEmpty()) {
            if (PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                canUse = true;
            }
        } else {
            for (Role role : member.getRoles()) {
                if (roles.contains(role.getId()) || PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                    canUse = true;
                    break;
                }
            }
        }
        if (!canUse) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.general.cant-use-this-command"), channel);
            return;
        }

        AudioEngine.stop(message.getChannel().asTextChannel());
    }
}
