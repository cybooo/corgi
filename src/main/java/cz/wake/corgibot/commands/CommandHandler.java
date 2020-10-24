package cz.wake.corgibot.commands;

import cz.wake.corgibot.commands.admin.Ignore;
import cz.wake.corgibot.commands.admin.LeaveGuild;
import cz.wake.corgibot.commands.admin.Say;
import cz.wake.corgibot.commands.admin.SetPrefix;
import cz.wake.corgibot.commands.games.McStatus;
import cz.wake.corgibot.commands.mod.*;
import cz.wake.corgibot.commands.owner.Eval;
import cz.wake.corgibot.commands.owner.GuildList;
import cz.wake.corgibot.commands.owner.Log;
import cz.wake.corgibot.commands.owner.Stop;
import cz.wake.corgibot.commands.user.*;
import cz.wake.corgibot.utils.CorgiLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<Command> commands = new ArrayList<>();

    public void registerCommand(Command c) {
        try {
            commands.add(c);
        } catch (Exception e) {
            CorgiLogger.warnMessage("Error during register command - " + c.getCommand() + " :");
            e.printStackTrace();
        }
    }

    public void unregisterCommand(Command c) {
        commands.remove(c);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public List<Command> getCommandsByType(CommandCategory type) {
        return commands.stream().filter(command -> command.getCategory() == type).collect(Collectors.toList());
    }

    public void register() {
        CorgiLogger.infoMessage("Loading all commands.");
        registerCommand(new EightBall());
        registerCommand(new Help());
        registerCommand(new Ping());
        registerCommand(new Roles());
        registerCommand(new UserInfo());
        registerCommand(new Stop());
        registerCommand(new Say());
        registerCommand(new Fact());
        registerCommand(new Uptime());
        registerCommand(new Emote());
        registerCommand(new McStatus());
        registerCommand(new Archive());
        registerCommand(new Purge());
        registerCommand(new TextToBlock());
        registerCommand(new Giveaway());
        registerCommand(new Stats());
        registerCommand(new About());
        registerCommand(new Invite());
        registerCommand(new Changelog());
        registerCommand(new GuildList());
        registerCommand(new Log());
        registerCommand(new Kick());
        registerCommand(new Ban());
        registerCommand(new SetPrefix());
        registerCommand(new RoleInfo());
        registerCommand(new Avatar());
        registerCommand(new LeaveGuild());
        registerCommand(new Ignore());
        registerCommand(new Cat());
        registerCommand(new Dog());
        registerCommand(new GuildInfo());
        registerCommand(new Eval());
        registerCommand(new Choose());
        //registerCommand(new Support());
        registerCommand(new Reminder());
        registerCommand(new Color());
        registerCommand(new Twitter());
        registerCommand(new Love());
        //registerCommand(new Lang());
        registerCommand(new Pin());
        registerCommand(new Bigmoji());
        registerCommand(new Hug());
        CorgiLogger.greatMessage("Corgi will respond to (" + commands.size() + ") commands.");
    }

    public Command getCommand(String name) {
        Optional<Command> cmd = commands.stream().filter(c -> c.getCommand().equals(name)).findFirst();
        if (cmd.isPresent()) {
            return cmd.get();
        }
        cmd = commands.stream().filter(c -> Arrays.asList(c.getAliases()).contains(name)).findFirst();
        return cmd.orElse(null);
    }


}
