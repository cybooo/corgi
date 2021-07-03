package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@CommandInfo(
        name = "guildinfo",
        aliases = {"serverinfo"},
        description = "Displays information about the server where the command is written.",
        help = "%guildinfo - View info about server",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.0")
public class GuildInfo implements CommandBase {

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
                .setAuthor("Guild info", null, guild.getIconUrl())
                .setColor(guild.getOwner().getColor() == null ? Constants.DEFAULT_PURPLE : guild.getOwner().getColor())
                .setDescription("Information for " + guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("Users (Online/Unique)", (int) guild.getMembers().stream().filter(u -> !u.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count() + "/" + guild.getMembers().size(), true)
                .addField("Date created", guild.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField("Voice/Text channels", guild.getVoiceChannels().size() + "/" + guild.getTextChannels().size(), true)
                .addField("Owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
                .addField("Region", guild.getRegion() == null ? "Unknown." : guild.getRegion().getName(), true)
                .addField("Roles (" + guild.getRoles().size() + ")", roles, false)
                .setFooter("Server ID: " + guild.getId(), null)
                .build()
        ).queue();

    }

}
