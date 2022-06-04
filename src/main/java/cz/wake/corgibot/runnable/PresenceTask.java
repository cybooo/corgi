package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.api.entities.Activity;

public class PresenceTask implements Runnable {

    @Override
    public void run() {
        CorgiBot.getJda().getPresence().setActivity(Activity.watching(CorgiBot.getJda().getGuilds().size() + " servers | corgibot.xyz"));
    }

}
