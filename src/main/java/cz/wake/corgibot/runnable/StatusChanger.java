package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.core.entities.Game;

import java.util.TimerTask;

public class StatusChanger extends TimerTask {

    private int sInt = 0;

    @Override
    public void run() {
        if(sInt == 0){
            CorgiBot.getJda().getPresence().setGame(Game.of(".invite"));
            sInt++;
        } else if (sInt == 1){
            CorgiBot.getJda().getPresence().setGame(Game.of(".help"));
            sInt++;
        } else if (sInt == 2){
            CorgiBot.getJda().getPresence().setGame(Game.of(CorgiBot.getJda().getUsers().size() + " users"));
            sInt++;
        } else if (sInt == 3){
            CorgiBot.getJda().getPresence().setGame(Game.of(CorgiBot.getJda().getGuilds().size() + " guilds"));
            sInt = 0;
        }
    }
}
