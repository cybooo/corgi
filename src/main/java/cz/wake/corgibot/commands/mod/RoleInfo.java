package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;

@SinceCorgi(version = "1.2")
public class RoleInfo implements Command {

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
            MessageUtils.sendErrorMessage("Nebyla nalezena žádná role.", channel);
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(role.getColor());
        embed.addField(role.getName(), "ID: " + role.getId(), true);
        embed.addField("Pozice", String.valueOf(role.getPosition()), true);
        embed.addField("Oddělená", String.valueOf(role.isHoisted()), true);
        embed.addField("Spravovaná", String.valueOf(role.isManaged()), true);
        embed.addField("Označitelná", String.valueOf(role.isMentionable()), true);
        embed.addField("Datum vytvoření", CorgiBot.getInstance().formatTime(role.getCreationTime().toLocalDateTime()), true);

        channel.sendMessage(embed.build()).queue();
    }

    @Override
    public String getCommand() {
        return "roleinfo";
    }

    @Override
    public String getDescription() {
        return "Zobrazení informací o požadované roli.";
    }

    @Override
    public String getHelp() {
        return "%roleinfo nazev/ID";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rinfo"};
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }
}
