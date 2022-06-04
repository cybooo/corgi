package cz.wake.corgibot.commands.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.music.AudioEngine;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
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
        description = "Plays a specified song.",
        help = "%play <URL> - Play a song.",
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
            MessageUtils.sendErrorMessage("You can't use this command!", channel);
            return;
        }

        if (args.length == 0) {
            MessageUtils.sendErrorMessage("You need to specify the URL!\nExample: **" + gw.getPrefix() + "play https://www.youtube.com/watch?v=dQw4w9WgXcQ**", channel);
        } else {
            if (member.getVoiceState().getChannel() == null) {
                MessageUtils.sendErrorMessage("You need to be connected in a voice channel!", channel);
                return;
            }

            if (message.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
                if (!Objects.equals(member.getVoiceState().getChannel(), message.getGuild().getSelfMember().getVoiceState().getChannel())) {
                    MessageUtils.sendErrorMessage("You need to be connected in the same voice channel as me!", channel);
                    return;
                }
            }
            AudioEngine.loadAndPlay(member, channel, member.getVoiceState().getChannel(), args[0]);
        }
    }
}
