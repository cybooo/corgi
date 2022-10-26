package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.api.entities.Activity;

public record PresenceTask() implements Runnable {

    @Override
    public void run() {
        CorgiBot.getShardManager().setActivity(Activity.watching("c!help | " + CorgiBot.getShardManager().getGuildCache().size() + " servers"));
    }
}
