package cz.wake.corgibot.commands;

import cz.wake.corgibot.commands.admin.*;
import cz.wake.corgibot.commands.mod.ArchiveCommand;
import cz.wake.corgibot.commands.mod.RolesCommand;
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
        registerCommand(new EightBallCommand());
        registerCommand(new HelpCommand());
        registerCommand(new PingCommand());
        registerCommand(new PlayerStatsCommand());
        registerCommand(new RolesCommand());
        registerCommand(new UserInfoCommand());
        registerCommand(new StopCommand());
        registerCommand(new SayCommand());
        registerCommand(new FactCommand());
        registerCommand(new UptimeCommand());
        registerCommand(new EmoteCommand());
        registerCommand(new StatusCommand());
        registerCommand(new MemeCommand());
        registerCommand(new ArchiveCommand());
        registerCommand(new AtsCommand());
        //registerCommand(new PurgeCommand());
        registerCommand(new TextToBlock());
        registerCommand(new TrumpCommand());
        registerCommand(new GiveawayCommand());
        registerCommand(new BotStatsCommand());
    }


}
