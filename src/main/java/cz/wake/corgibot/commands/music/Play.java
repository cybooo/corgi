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
import java.util.Objects;

@CommandInfo(
        name = "play",
        aliases = {"playsong"},
        description = "commands.music-play.description",
        help = "commands.music-play.help",
        category = CommandCategory.MUSIC
)
@SinceCorgi(version = "1.3.5")
public class Play implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        boolean canUse = false;
        ArrayList<String> roles = CorgiBot.getInstance().getSql().getRoleMusicRoles(message.getGuild().getId(), getCommand());

        if (member.getRoles().isEmpty()) {
            if (roles.isEmpty() || PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                canUse = true;
            }
        } else {
            for (Role role : member.getRoles()) {
                if (roles.isEmpty() || roles.contains(role.getId()) || PermissionUtil.checkPermission(member, Permission.MANAGE_CHANNEL)) {
                    canUse = true;
                    break;
                }
            }
        }
        if (!canUse) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.general.cant-use-this-command"), channel);
            return;
        }

        if (args.length == 0) {
            MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.music-play.specify-url"), gw.getPrefix()), channel);
        } else {
            if (member.getVoiceState().getChannel() == null) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.music.need-to-be-in-voice"), channel);
                return;
            }

            if (message.getGuild().getSelfMember().getVoiceState().getChannel() != null) {
                if (!Objects.equals(member.getVoiceState().getChannel(), message.getGuild().getSelfMember().getVoiceState().getChannel())) {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.music.need-to-be-in-same-voice"), channel);
                    return;
                }
            }
            AudioEngine.loadAndPlay(member, channel, member.getVoiceState().getChannel(), args[0]);
        }
    }
}
