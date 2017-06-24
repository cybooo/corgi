package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class RolesCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Server Roles**\n```\n");
        for (Role role : channel.getGuild().getRoles()) {
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
        return "Seznam rolí";
    }

    @Override
    public CommandType getType() {
        return CommandType.WAKE;
    }
}
