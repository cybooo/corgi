package cz.wake.corgibot.listener;

import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        var shard = event.getJDA();
        var shardInfo = event.getJDA().getShardInfo();

        BotManager.loadGuilds(shardInfo.getShardId());

        shard.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("c!help | " + event.getJDA().getGuildCache().size() + " servers"));

        CorgiLogger.infoMessage("Loaded shard " + (shardInfo.getShardId() + 1) + "/" + shardInfo.getShardTotal() +
                " with " + shard.getGuilds().size() + " guilds.");

    }
}
