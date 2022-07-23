package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@CommandInfo(
        name = "guildinfo",
        aliases = {"serverinfo"},
        description = "commands.guild-info.description",
        help = "commands.guild-info.help",
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

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(I18n.getLoc(gw, "commands.guild-info.info"), null, guild.getIconUrl())
                .setColor(guild.getOwner().getColor() == null ? Constants.BLUE : guild.getOwner().getColor())
                .setDescription(String.format(I18n.getLoc(gw, "commands.guild-info.info-about"), guild.getName()))
                .setThumbnail(guild.getIconUrl())
                .addField(I18n.getLoc(gw, "commands.guild-info.users"), (int) guild.getMembers().stream().filter(u -> !u.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count() + "/" + guild.getMembers().size(), true)
                .addField(I18n.getLoc(gw, "commands.guild-info.date-created"), guild.getTimeCreated().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll("[^0-9.:-]", " "), true)
                .addField(I18n.getLoc(gw, "commands.guild-info.voice-text"), guild.getVoiceChannels().size() + "/" + guild.getTextChannels().size(), true)
                .addField(I18n.getLoc(gw, "commands.guild-info.owner"), guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
                // .addField(I18n.getLoc(gw, "commands.guild-info.region"), guild.getRegion() == null ? I18n.getLoc(gw, "commands.guild-info.unknown") : guild.getRegion().getName(), true)
                .addField(I18n.getLoc(gw, "commands.guild-info.roles") + " (" + guild.getRoles().size() + ")", roles, false)
                .setFooter(I18n.getLoc(gw, "commands.guild-info.server-id") + guild.getId(), null)
                .build()
        ).queue();

    }

}
