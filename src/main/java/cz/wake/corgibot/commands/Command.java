package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class Command {

    protected String name = "null";
    protected String description = "No description available.";
    protected String extendedDescription = null;
    protected Category category = new Category("unassigned");
    protected CommandPermission commandPermission = CommandPermission.USER;
    protected CommandState commandState = CommandState.GUILD;
    protected ArrayList<String> usage = new ArrayList<>();
    protected int cooldown = 0;
    protected CooldownScope cooldownScope = CooldownScope.USER;
    protected Permission[] requiredUserPerms = new Permission[0];
    protected Permission[] requiredBotPerms = new Permission[0];
    protected String[] aliases = new String[0];
    protected Command[] subcommands = new Command[0];

    public abstract void onExecuted(CommandEvent event) throws Throwable;

    public final void run(CommandEvent event) {
        if (event.getArgs().length > 0) {
            String[] args = event.getArgs();
            for (Command cmd : subcommands) {
                if (cmd.isCommandFor(args[0])) {
                    event.setArgs(args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
                    cmd.run(event);
                    return;
                }
            }
        }

        if (commandPermission == CommandPermission.OWNER && !(event.isBotOwner())) {
            terminate(event, "Unfortunately, only the bot owner can use this command.");
            return;
        }

        if (commandPermission == CommandPermission.ADMIN && !(event.isBotAdmin())) {
            terminate(event, "Unfortunately, only a bot admin can use this command.");
            return;
        }

        if (commandPermission == CommandPermission.MOD && !(event.isBotMod())) {
            terminate(event, "Unfortunately, only a bot mod can use this command.");
            return;
        }

        if (category != null && !category.test(event)) {
            terminate(event, category.getFailMessage());
            return;
        }


        if (commandState.equals(CommandState.DM) && event.isFromType(ChannelType.TEXT)) {
            terminate(event, "This command can't be used outside of DMs.");
            return;
        } else if (event.isFromType(ChannelType.TEXT)) {
            for (Permission p : requiredBotPerms) {
                if (p.isChannel()) {
                    if (p.name().startsWith("VOICE")) {
                        VoiceChannel vc = event.getMember().getVoiceState().getChannel();
                        if (vc == null) {
                            terminate(event, CorgiBot.respond(Action.GET_IN_VOICE_CHANNEL, event.getLocale()));
                            return;
                        } else if (!PermissionUtil.checkPermission(vc, event.getSelfMember(), p)) {
                            terminate(event, CorgiBot.respond(Action.NOPERM_BOT, event.getLocale(), "`" + p.getName() + "` (Voice Channel Permission)"));
                            return;
                        }
                    } else {
                        if (!PermissionUtil.checkPermission(event.getTextChannel(), event.getSelfMember(), p)) {
                            terminate(event, CorgiBot.respond(Action.NOPERM_BOT, event.getLocale(), "`" + p.getName() + "` (Channel Permission)"));
                            return;
                        }
                    }
                } else {
                    if (!PermissionUtil.checkPermission(event.getTextChannel(), event.getSelfMember(), p)) {
                        terminate(event, CorgiBot.respond(Action.NOPERM_BOT, event.getLocale(), "`" + p.getName() + "`"));
                        return;
                    }
                }
            }

            for (Permission p : requiredUserPerms) {
                if (p.isChannel()) {
                    if (!PermissionUtil.checkPermission(event.getTextChannel(), event.getMember(), p)) {
                        terminate(event, CorgiBot.respond(Action.NOPERM_USER, event.getLocale(), "`" + p.getName() + " (Channel Permission)`"));
                        return;
                    }
                } else {
                    if (!PermissionUtil.checkPermission(event.getTextChannel(), event.getMember(), p)) {
                        terminate(event, CorgiBot.respond(Action.NOPERM_USER, event.getLocale(), "`" + p.getName() + "`"));
                        return;
                    }
                }
            }
        } else if (commandState.equals(CommandState.GUILD)) {
            terminate(event, "This command can't be used in DMs.");
            return;
        }

        if (cooldown > 0) {
            String key = getCooldownKey(event);
            int remaining = event.getClient().getRemainingCooldown(key);
            if (remaining > 0) {
                String error = getCooldownError(remaining);
                if (error != null) {
                    terminate(event, error);
                    return;
                }
            } else event.getClient().applyCooldown(key, cooldown);
        }

        try {
            onExecuted(event);
        } catch (Throwable t) {
            throwException(t, event);
        }
    }

    public String getCooldownKey(CommandEvent event) {
        switch (cooldownScope) {
            case USER:
                return cooldownScope.genKey(name, event.getAuthor().getIdLong());
            case USER_GUILD:
                return event.getGuild() != null ? cooldownScope.genKey(name, event.getAuthor().getIdLong(), event.getGuild().getIdLong()) :
                        CooldownScope.USER_CHANNEL.genKey(name, event.getAuthor().getIdLong(), event.getChannel().getIdLong());
            case USER_CHANNEL:
                return cooldownScope.genKey(name, event.getAuthor().getIdLong(), event.getChannel().getIdLong());
            default:
                return "";
        }
    }

    public String getCooldownError(int remaining) {
        if (remaining <= 0)
            return null;
        String front = "That command is on cooldown for " + remaining + " more seconds";
        if (cooldownScope.equals(CooldownScope.USER))
            return front + "!";
        else if (cooldownScope.equals(CooldownScope.USER_GUILD))
            return front + " " + CooldownScope.USER_CHANNEL.errorSpecfication + "!";
        else return front + " " + cooldownScope.errorSpecfication + "!";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public String[] getAliases() {
        return aliases;
    }

    public ArrayList<String> getUsage() {
        return usage;
    }

    public boolean isCommandFor(String input) {
        if (name.equalsIgnoreCase(input)) return true;
        for (String alias : aliases)
            if (alias.equalsIgnoreCase(input))
                return true;
        return false;
    }

    private void terminate(CommandEvent event, String message) {
        if (message != null)
            event.getChannel().sendMessage(message).queue();
    }

    protected void addUsage(String usage) {
        this.usage.add("%s" + usage);
    }

    protected boolean isMention(String s) {
        return s.matches("<@!?(\\d+)>");
    }

    protected void throwException(Throwable t, CommandEvent event) {
        throwException(t, event, "No Description Provided.");
    }

    protected void throwException(Throwable t, CommandEvent event, String description) {
        String endl = System.getProperty("line.separator");
        String s = CorgiBot.respond(Action.EXCEPTION_THROWN, event.getLocale()) + endl + endl + "Description: " + description + endl + "Command: " + this.name + endl + endl + ExceptionUtils.getStackTrace(t);
        try {
            byte[] b = s.getBytes("UTF-8");
            event.getChannel().sendFile(b, "traceback.txt", new MessageBuilder("An error has occurred! This should be reported to the dev right away! Use the `" + event.getPrefix() + "ticket` command to do so, don't forget to show this file, too.").build()).queue();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    protected enum CommandState {

        DM, GUILD, BOTH;

        CommandState() {
        }

        ;
    }

    protected enum CommandPermission {
        USER, MOD, ADMIN, OWNER;

        CommandPermission() {
        }

        ;
    }

    protected enum CooldownScope {
        USER("U:%d", ""),
        USER_CHANNEL("U:%d|C:%d", "in this channel"),
        USER_GUILD("U:%d|G:%d", "in this server");

        private final String format;
        final String errorSpecfication;

        CooldownScope(String format, String errorSpecification) {
            this.format = format;
            this.errorSpecfication = errorSpecification;
        }

        String genKey(String name, long id) {
            return genKey(name, id, -1);
        }

        String genKey(String name, long id1, long id2) {
            if (id2 == -1)
                return name + "|" + String.format(format, id1);
            else return name + "|" + String.format(format, id1, id2);
        }
    }

    public static class Category {
        private final String name;
        private String failMessage;
        private final Predicate<CommandEvent> predicate;

        public Category(String name) {
            this.name = name;
            this.failMessage = null;
            this.predicate = null;
        }

        public Category(String name, String failMessage, Predicate<CommandEvent> predicate) {
            this.name = name;
            this.failMessage = failMessage;
            this.predicate = predicate;
        }

        public String getName() {
            return name;
        }

        public Predicate<CommandEvent> getPredicate() {
            return predicate;
        }

        public boolean test(CommandEvent event) {
            return predicate == null || predicate.test(event);
        }

        public String getFailMessage() {
            return failMessage;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Category))
                return false;

            Category other = (Category) o;
            return Objects.equals(name, other.name) && Objects.equals(predicate, other.predicate) && Objects.equals(failMessage, other.failMessage);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.name);
            hash = 17 * hash + Objects.hashCode(this.failMessage);
            hash = 17 * hash + Objects.hashCode(this.predicate);
            return hash;
        }
    }


}
