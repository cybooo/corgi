package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelDeleteEvent extends ListenerAdapter {

    @Override
    public void onChannelDelete(@NotNull net.dv8tion.jda.api.events.channel.ChannelDeleteEvent e) {

        if (BotManager.getCustomGuild(e.getGuild().getId()) == null) {
            return;
        }

        if (e.getChannel().getType() == ChannelType.TEXT) {

            try {
                // Delete from manager
                BotManager.getCustomGuild(e.getGuild().getId()).getIgnoredChannels().remove((MessageChannel) e.getChannel());

                // Delete from SQL
                CorgiBot.getInstance().getSql().deleteIgnoredChannel(e.getChannel().getId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
