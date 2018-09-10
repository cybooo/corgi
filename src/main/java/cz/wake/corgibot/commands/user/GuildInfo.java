package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@SinceCorgi(version = "1.0")
public class GuildInfo implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        Guild guild = member.getGuild();

        String roles = guild.getRoles().stream()
                .filter(role -> !guild.getPublicRole().equals(role))
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        if (roles.length() > 1024)
            roles = roles.substring(0, 1024 - 4) + "...";

        channel.sendMessage(new EmbedBuilder()
                .setAuthor("Informace o serveru", null, guild.getIconUrl())
                .setColor(guild.getOwner().getColor() == null ? Constants.BLUE : guild.getOwner().getColor())
                .setDescription("Informace pro server " + guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("Uživatelé (Online/Unikátní)", (int) guild.getMembers().stream().filter(u -> !u.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count() + "/" + guild.getMembers().size(), true)
                .addField("Datum vytvoření", guild.getCreationTime().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField("Voice/Text channels", guild.getVoiceChannels().size() + "/" + guild.getTextChannels().size(), true)
                .addField("Majitel", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
                .addField("Region", guild.getRegion() == null ? "Neznámý." : guild.getRegion().getName(), true)
                .addField("Role (" + guild.getRoles().size() + ")", roles, false)
                .setFooter("Server ID: " + String.valueOf(guild.getId()), null)
                .build()
        ).queue();

    }

    @Override
    public String getCommand() {
        return "guildinfo";
    }

    @Override
    public String getDescription() {
        return "Informace o serveru, kde je příkaz napsán!";
    }

    @Override
    public String getHelp() {
        return "%guildinfo - Zobrazení informací o serveru";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"serverinfo"};
    }
}
