package cz.wake.corgibot.utils.buttons;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public interface ButtonRunnable {

    void run(long ownerID, User user, Message message);
}
