package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

@CommandInfo(
        name = "roleinfo",
        aliases = {"rinfo"},
        description = "Displays info about a specified role",
        help = "%roleinfo name/ID",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.2")
public class RoleInfo implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        Role role = null;
        long id = 0;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            CorgiBot.LOGGER.error(e.toString());
        }
        if (id != 0)
            role = message.getGuild().getRoleById(String.valueOf(id));
        else if (id == 0) {
            for (Role role1 : message.getGuild().getRoles()) {
                if (role1.getName().equalsIgnoreCase(args[0]))
                    role = role1;
            }
        }
        if (role == null) {
            MessageUtils.sendErrorMessage("No roles found", channel);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(role.getColor());
        embed.addField(role.getName(), "ID: " + role.getId(), true);
        embed.addField("Position", String.valueOf(role.getPosition()), true);
        embed.addField("Separated", String.valueOf(role.isHoisted()), true);
        embed.addField("Managed", String.valueOf(role.isManaged()), true);
        embed.addField("Mentionable", String.valueOf(role.isMentionable()), true);
        embed.addField("Date created", CorgiBot.getInstance().formatTime(role.getTimeCreated().toLocalDateTime()), true);

        channel.sendMessage(embed.build()).queue();
    }
    
}
