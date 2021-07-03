package cz.wake.corgibot.commands;

import net.dv8tion.jda.api.Permission;

public class FinalCommand {

    private final CommandBase command;

    private final String name;

    private final String help;

    private final String description;

    private final CommandCategory commandCategory;

    private String[] aliases = {};

    private boolean beta = false;

    private boolean onlyOwner = false;

    private Permission[] reqUserPermissions = {};

    private Permission[] reqBotPermissions = {};

    public FinalCommand(CommandBase command, String name, String help, String description, CommandCategory commandCategory) {
        this.command = command;
        this.name = name;
        this.help = help;
        this.description = description;
        this.commandCategory = commandCategory;
    }

    public CommandBase getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public String getDescription() {
        return description;
    }

    public CommandCategory getCommandCategory() {
        return commandCategory;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean isBeta() {
        return beta;
    }

    public boolean isOnlyOwner() {
        return onlyOwner;
    }

    public Permission[] getReqUserPermissions() {
        return reqUserPermissions;
    }

    public Permission[] getReqBotPermissions() {
        return reqBotPermissions;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public void setBeta(boolean beta) {
        this.beta = beta;
    }

    public void setOnlyOwner(boolean onlyOwner) {
        this.onlyOwner = onlyOwner;
    }

    public void setReqUserPermissions(Permission[] reqUserPermissions) {
        this.reqUserPermissions = reqUserPermissions;
    }

    public void setReqBotPermissions(Permission[] reqBotPermissions) {
        this.reqBotPermissions = reqBotPermissions;
    }
}
