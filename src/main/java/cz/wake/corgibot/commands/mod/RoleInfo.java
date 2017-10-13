package cz.wake.corgibot.commands.mod;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

public class RoleInfo implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
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
        String perms = "";
        perms = role.getPermissions().stream().map((perm) -> perm.name()).map((p) -> "`, `" + p).reduce(perms, String::concat);
        embed.addField("Práva", perms.substring(3) + "`", false);

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
        return ".roleinfo nazev/ID";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rinfo"};
    }
}
