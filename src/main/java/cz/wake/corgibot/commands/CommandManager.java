package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.Beta;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.commands.admin.*;
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

public class CommandManager {

    private final List<FinalCommand> commands = new ArrayList<>();

    public void registerCommand(CommandBase command) {
        if (Arrays.stream(command.getClass().getAnnotations()).noneMatch(a -> a instanceof CommandInfo)) {
            CorgiBot.getLog(command.getClass()).error("Require CommandInfo annotation!");
            return;
        }

        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        FinalCommand finalCommand = new FinalCommand(command, info.name(), info.help(), info.description(), info.category());
        if (info.aliases().length > 0) finalCommand.setAliases(info.aliases());
        if (info.userPerms().length > 0) finalCommand.setReqUserPermissions(info.userPerms());
        if (info.botPerms().length > 0) finalCommand.setReqBotPermissions(info.botPerms());
        if (Arrays.stream(command.getClass().getAnnotations()).anyMatch(a -> a instanceof OnlyOwner))
            finalCommand.setOnlyOwner(true);
        if (Arrays.stream(command.getClass().getAnnotations()).anyMatch(a -> a instanceof Beta))
            finalCommand.setBeta(true);
        commands.add(finalCommand);
    }

    public void unregisterCommand(String name) {
        commands.removeIf(c -> c.getName().equalsIgnoreCase(name));
    }

    public FinalCommand getCommand(String name) {
        Optional<FinalCommand> cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
        if (cmd.isPresent()) {
            return cmd.get();
        }
        cmd = commands.stream().filter(c -> Arrays.asList(c.getAliases()).contains(name)).findFirst();
        return cmd.orElse(null);
    }

    public List<FinalCommand> getCommandsByCategory(CommandCategory category) {
        return commands.stream().filter(command -> command.getCommandCategory() == category).collect(Collectors.toList());
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
        registerCommand(new Dice());
        registerCommand(new Covid());
        CorgiLogger.greatMessage("Corgi will respond to (" + commands.size() + ") commands.");
    }

    public List<FinalCommand> getCommands() {
        return commands;
    }
}
