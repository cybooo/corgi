package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.admin.Ignore;
import cz.wake.corgibot.commands.admin.LeaveGuild;
import cz.wake.corgibot.commands.admin.Say;
import cz.wake.corgibot.commands.admin.SetPrefix;
import cz.wake.corgibot.commands.mod.*;
import cz.wake.corgibot.commands.owner.Eval;
import cz.wake.corgibot.commands.owner.GuildList;
import cz.wake.corgibot.commands.owner.Log;
import cz.wake.corgibot.commands.owner.Stop;
import cz.wake.corgibot.commands.user.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler {

    public static List<ICommand> commands = new ArrayList<>();

    public void registerCommand(ICommand c) {
        try {
            commands.add(c);
            CorgiBot.LOGGER.info("Příkaz ." + c.getCommand() + " byl úspěšně zaregistrován!");
        } catch (Exception e) {
            CorgiBot.LOGGER.info("Chyba při registraci příkazu ." + c.getCommand());
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
        registerCommand(new Purge());
        registerCommand(new TextToBlock());
        registerCommand(new Giveaway());
        registerCommand(new BotStats());
        registerCommand(new Perms());
        registerCommand(new About());
        registerCommand(new FullWidth());
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
    }


}
