package cz.wake.corgibot.commands;

import cz.wake.corgibot.commands.admin.*;
import cz.wake.corgibot.commands.mod.*;
import cz.wake.corgibot.commands.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<ICommand> commands = new ArrayList<>();

    public void registerCommand(ICommand c) {
        try {
            commands.add(c);
            System.out.println("[BOT]: Prikaz ." + c.getCommand() + " byl uspesne zaregistrovan.");
        } catch (Exception e) {
            System.out.println("[BOT]: Chyba pri registraci prikazu " + c.getCommand() + ".");
        }
    }

    public void unregisterCommand(ICommand c) {
        commands.remove(c);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public List<ICommand> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    public void register() {
        registerCommand(new EightBall());
        registerCommand(new Help());
        registerCommand(new Ping());
        registerCommand(new PlayerStats());
        registerCommand(new Roles());
        registerCommand(new UserInfo());
        registerCommand(new Stop());
        registerCommand(new Say());
        registerCommand(new Fact());
        registerCommand(new Uptime());
        registerCommand(new Emote());
        registerCommand(new Status());
        registerCommand(new Meme());
        registerCommand(new Archive());
        registerCommand(new Ats());
        registerCommand(new Purge());
        registerCommand(new TextToBlock());
        registerCommand(new Trump());
        registerCommand(new Giveaway());
        registerCommand(new BotStats());
    }


}
