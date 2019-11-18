package cz.wake.corgibot.runnable;

import cz.wake.corgibot.listener.ChatListener;

import java.util.TimerTask;

public class SpamHandler extends TimerTask {

    private ChatListener ch = new ChatListener();

    @Override
    public void run() {
        ch.clearSpamMap();
    }
}
