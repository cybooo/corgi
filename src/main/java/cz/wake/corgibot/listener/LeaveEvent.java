package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.OffsetDateTime;

public class LeaveEvent extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        CorgiBot.getInstance().getGuildLogChannel().sendMessage(MessageUtils.getEmbed(Constants.RED)
                .setThumbnail(event.getGuild().getIconUrl())
                .setFooter(event.getGuild().getId(), event.getGuild().getIconUrl())
                .setTimestamp(OffsetDateTime.now())
                .setTitle("Corgi has left a guild!")
                .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                .setDescription("Guild name: `" + event.getGuild().getName() + "` :broken_heart:\n" +
                        "Owner: " + (event.getGuild().getOwner() != null ?
                        event.getGuild().getOwner().getUser().getName()
                        : "Does not exist, or unable to find!")).build()).queue();

        // Logger
        CorgiLogger.infoMessage("GuildLeaveEvent - " + event.getGuild().getName() + "(" + event.getGuild().getId() + ")");

        // Delete all ignored channels from guild
        CorgiBot.getInstance().getSql().deleteAllIgnoredChannels(event.getGuild().getId());

        // Delete form manager
        BotManager.removeGuild(BotManager.getCustomGuild(event.getGuild().getId()));
    }
}
