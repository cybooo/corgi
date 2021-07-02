package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.CommandHandler;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;

@SinceCorgi(version = "0.1")
public class Help implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            if (channel.getType() == ChannelType.TEXT) {
                channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setTitle("Check your messages!")
                        .setDescription(EmoteList.MAILBOX + " | I have sent the help to your DMs!").build()).queue();
            }
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE)
                    .setAuthor("Corgi's commands", null, channel.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(getContext(member, message.getGuild())).setFooter("You can find all commands on: https://corgibot.xyz/commands", null)
                    .build()).queue());
        } else {
            String commandName = args[0];
            CommandHandler ch = new CommandHandler();
            StringBuilder sb = new StringBuilder();
            //Normal
            ch.getCommands().stream().filter(c -> c.getCommand().equalsIgnoreCase(commandName)).forEach(c -> {
                for (String s : c.getAliases()) {
                    sb.append(s).append(", ");
                }
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Help for command - " + commandName + " :question:")
                        .setDescription(c.getDescription() + "\n\n**Usage**\n" + c.getHelp().replace("%", gw.getPrefix()))
                        .setColor(Constants.DEFAULT_PURPLE).setFooter("Aliases: " + String.join(", ", c.getAliases()), null).build()).queue();
            });
        }
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Basic help for Corgi";
    }

    @Override
    public String getHelp() {
        return "%help - Sends a list of commands to your DMs!\n" +
                "%help [příkaz] - Shows info about commands and its usage.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands"};
    }

    private StringBuilder getContext(Member member, Guild guild) {
        StringBuilder builder = new StringBuilder();
        CommandHandler ch = new CommandHandler();
        try {
            builder.append("Prefix for commands on ").append(guild.getName()).append(" is `").append(BotManager.getCustomGuild(member.getGuild().getId()).getPrefix()).append("`\nView additional info using `").append(BotManager.getCustomGuild(member.getGuild().getId()).getPrefix()).append("help <command>`");
        } catch (NullPointerException ex){
            builder.append("Prefix for commands is `c!`\nView additional info using `c!help <command>`");
        }
        for (CommandCategory type : CommandCategory.getTypes()) {
            if (type == CommandCategory.MUSIC || type == CommandCategory.BOT_OWNER || type == CommandCategory.HIDDEN) { // Neexistujici kategorie (zatim)
                return builder;
            }
            builder.append("\n\n");
            builder.append(type.getEmote()).append(" | **").append(type.formattedName()).append("** - ").append(ch.getCommandsByType(type).size()).append("\n");
            for (Command c : ch.getCommands()) {
                if (c.getCategory().equals(type)) {
                    builder.append("`").append(c.getCommand()).append("` ");
                }
            }
        }
        return builder;
    }

}
