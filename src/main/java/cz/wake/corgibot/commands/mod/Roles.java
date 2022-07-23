package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

@CommandInfo(
        name = "roles",
        description = "commands.roles.description",
        help = "commands.roles.help",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.1")
public class Roles implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        StringBuilder sb = new StringBuilder();
        sb.append("**").append(I18n.getLoc(gw, "commands.roles.server-roles")).append("**\n```\n");
        for (Role role : member.getGuild().getRoles()) {
            sb.append(role.getName()).append(" (").append(role.getId()).append(")\n");
        }
        sb.append("```");

        channel.sendMessage(sb.toString()).queue();
    }

}
