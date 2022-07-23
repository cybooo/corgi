package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

@CommandInfo(
        name = "roleinfo",
        aliases = {"rinfo"},
        description = "commands.role-info.description",
        help = "commands.role-info.help",
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
        else {
            for (Role role1 : message.getGuild().getRoles()) {
                if (role1.getName().equalsIgnoreCase(args[0]))
                    role = role1;
            }
        }
        if (role == null) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.role-info.no-role-found"), channel);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(role.getColor());
        embed.addField(role.getName(), I18n.getLoc(gw, "commands.role-info.id") + " " + role.getId(), true);
        embed.addField(I18n.getLoc(gw, "commands.role-info.position"), String.valueOf(role.getPosition()), true);
        embed.addField(I18n.getLoc(gw, "commands.role-info.separated"), String.valueOf(role.isHoisted()), true);
        embed.addField(I18n.getLoc(gw, "commands.role-info.managed"), String.valueOf(role.isManaged()), true);
        embed.addField(I18n.getLoc(gw, "commands.role-info.mentionable"), String.valueOf(role.isMentionable()), true);
        embed.addField(I18n.getLoc(gw, "commands.role-info.date-created"), CorgiBot.getInstance().formatTime(role.getTimeCreated().toLocalDateTime()), true);

        channel.sendMessageEmbeds(embed.build()).queue();
    }

}
