package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;

public class LeaveEvent extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        CorgiBot.getInstance().getGuildLogChannel().sendMessage(MessageUtils.getEmbed(Constants.RED)
                .setThumbnail(event.getGuild().getIconUrl())
                .setFooter(event.getGuild().getId(), event.getGuild().getIconUrl())
                .setTimestamp(OffsetDateTime.now())
                .setTitle("Corgi byl vyhozen z guildy")
                .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                .setDescription("Nazev guildy: `" + event.getGuild().getName() + "` :broken_heart:\n" +
                        "Majitel: " + (event.getGuild().getOwner() != null ?
                        event.getGuild().getOwner().getUser().getName()
                        : "Neexistuje, nebo nelze zjistit!")).build()).queue();

        // Logger
        CorgiBot.LOGGER.info("GuildLeveEvent - " + event.getGuild().getName() + "(" + event.getGuild().getId() + ")");

        // Smazani vsech ignored channelu z guildy
        CorgiBot.getInstance().getSql().deleteAllIgnoredChannels(event.getGuild().getId());

        // Smazani z manageru
        BotManager.removeGuild(BotManager.getCustomGuild(event.getGuild().getId()));
    }
}
