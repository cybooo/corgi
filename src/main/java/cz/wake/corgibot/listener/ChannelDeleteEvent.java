package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ChannelDeleteEvent extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(@NotNull TextChannelDeleteEvent e) {

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
