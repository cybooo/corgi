package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.entities.*;

@SinceCorgi(version = "1.1")
public class Roles implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
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
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
