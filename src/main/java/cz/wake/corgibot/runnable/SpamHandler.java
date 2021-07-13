package cz.wake.corgibot.runnable;

import cz.wake.corgibot.listener.ChatListener;

public class SpamHandler implements Runnable {

    private final ChatListener ch = new ChatListener();

    @Override
    public void run() {
        ch.clearSpamMap();
    }
}
