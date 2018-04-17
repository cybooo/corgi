package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;

@SinceCorgi(version = "1.1")
public class Roles implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Server Roles**\n```\n");
        for (Role role : member.getGuild().getRoles()) {
            sb.append(role.getName()).append(" (").append(role.getId()).append(")\n");
        }
        sb.append("```");

        channel.sendMessage(sb.toString()).queue();
    }

    @Override
    public String getCommand() {
        return "roles";
    }

    @Override
    public String getDescription() {
        return "Seznam rolí na serveru.";
    }

    @Override
    public String getHelp() {
        return "%roles";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }
}
