package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent e) {

        if (e.getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (BotManager.getCustomGuild(e.getGuild().getId()) == null) {
            return;
        }

        try {
            // Delete from manager
            BotManager.getCustomGuild(e.getGuild().getId()).getIgnoredChannels().remove(e.getChannel());

            // Delete from SQL
            CorgiBot.getInstance().getSql().deleteIgnoredChannel(e.getChannel().getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
