package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.user.About;
import cz.wake.corgibot.commands.user.Ping;
import cz.wake.corgibot.utils.CorgiLogger;

public class CommandRegister {

    private CommandClient client = CorgiBot.getCommandClient();

    public void start() {
        CorgiLogger.infoMessage("Probehne registrace prikazu!");

        client.addCommand(new Ping());
        client.addCommand(new About());

        CorgiLogger.greatMessage("Corgi zaregistroval (" + client.getCommands().size() + ") prikazu.");
    }


}
