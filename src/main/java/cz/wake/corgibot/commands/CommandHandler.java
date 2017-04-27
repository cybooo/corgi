package cz.wake.corgibot.commands;

import cz.wake.corgibot.commands.admin.RolesCommand;
import cz.wake.corgibot.commands.admin.StopCommand;
import cz.wake.corgibot.commands.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<Command> commands = new ArrayList<>();

    public void registerCommand(Command c) {
        commands.add(c);
    }

    public void unregisterCommand(Command c) {
        commands.remove(c);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public List<Command> getCommandsByType(CommandType type) {
        return commands.stream().filter(command -> command.getType() == type).collect(Collectors.toList());
    }

    public void register() {
        registerCommand(new GitCommand());
        registerCommand(new EightBallCommand());
        registerCommand(new HelpCommand());
        registerCommand(new PingCommand());
        registerCommand(new PlayerStatsCommand());
        registerCommand(new RolesCommand());
        registerCommand(new UserInfoCommand());
        registerCommand(new StopCommand());
    }


}
