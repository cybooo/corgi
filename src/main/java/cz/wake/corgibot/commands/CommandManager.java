package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.Beta;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.commands.admin.*;
import cz.wake.corgibot.commands.games.McStatus;
import cz.wake.corgibot.commands.mod.*;
import cz.wake.corgibot.commands.music.*;
import cz.wake.corgibot.commands.owner.*;
import cz.wake.corgibot.commands.user.*;
import cz.wake.corgibot.utils.CorgiLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandManager {

    private final List<FinalCommand> commands = new ArrayList<>();

    public void registerCommands(CommandBase... commandsToRegister) {
        long time = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> {
            for (int i = 0, commandsToRegisterLength = commandsToRegister.length; i < commandsToRegisterLength; i++) {
                CommandBase command = commandsToRegister[i];
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
            CorgiLogger.greatMessage("Registered " + commands.size() + " commands in " + (System.currentTimeMillis() - time) + "ms");
        });
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
        registerCommands(
                new EightBall(),
                new Help(),
                new Ping(),
                new Roles(),
                new UserInfo(),
                new StopCorgi(),
                new Say(),
                new Fact(),
                new Uptime(),
                new Emote(),
                new McStatus(),
                new Archive(),
                new Purge(),
                new TextToBlock(),
                new Giveaway(),
                new BotStats(),
                new About(),
                new Invite(),
                new Changelog(),
                new GuildList(),
                new Log(),
                new Kick(),
                new Ban(),
                new SetPrefix(),
                new RoleInfo(),
                new Avatar(),
                new LeaveGuild(),
                new Ignore(),
                new Cat(),
                new Dog(),
                new GuildInfo(),
                new Eval(),
                new Choose(),
                new Reminder(),
                new Color(),
                new Twitter(),
                new Love(),
                new Pin(),
                new Bigmoji(),
                new Hug(),
                new Dice(),
                new Covid(),
                new AllowMusic(),
                new DisallowMusic(),
                new NowPlaying(),
                new Play(),
                new Skip(),
                new Stop(),
                new Volume(),
                new SendChangelog(),
                new ToggleBeta(),
                new Support(),
                new Stats());

        // registerCommand(new CreateTicketEmbed());
        // registerCommand(new Ticket());
        // registerCommand(new DisableSlashNotice());
        // registerCommand(new Support());
        // registerCommand(new Lang());
    }

    public List<FinalCommand> getCommands() {
        return commands;
    }
}
