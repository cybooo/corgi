package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.CommandManager;
import cz.wake.corgibot.commands.FinalCommand;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.*;

@CommandInfo(
        name = "help",
        aliases = {"commands"},
        description = "Basic help for Corgi",
        help = "%help - Sends a list of commands to your DMs!\n" +
                "%help [příkaz] - Shows info about commands and its usage.",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.1")
public class Help implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            if (channel.getType() == ChannelType.TEXT) {
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle("Check your messages!")
                        .setDescription(EmoteList.MAILBOX + " | I have sent the help to your DMs!").build()).queue();
            }
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                    .setAuthor("Corgi's commands", null, channel.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(getContext(member, message.getGuild())).setFooter("You can find all commands on: https://corgibot.xyz/commands", null)
                    .build()).queue());
        } else {
            String commandName = args[0];
            CommandManager cm = CorgiBot.getInstance().getCommandManager();
            StringBuilder sb = new StringBuilder();
            //Normal
            cm.getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).forEach(c -> {
                for (String s : c.getAliases()) {
                    sb.append(s).append(", ");
                }
                channel.sendMessageEmbeds(MessageUtils.getEmbed().setTitle("Help for command - " + commandName + " :question:")
                        .setDescription(c.getDescription() + "\n\n**Usage**\n" + c.getHelp().replace("%", gw.getPrefix()))
                        .setColor(Constants.BLUE).setFooter("Aliases: " + String.join(", ", c.getAliases()), null).build()).queue();
            });
        }
    }

    private StringBuilder getContext(Member member, Guild guild) {
        StringBuilder builder = new StringBuilder();
        CommandManager cm = CorgiBot.getInstance().getCommandManager();
        try {
            builder.append("Prefix for commands on ").append(guild.getName()).append(" is `").append(BotManager.getCustomGuild(member.getGuild().getId()).getPrefix()).append("`\nView additional info using `").append(BotManager.getCustomGuild(member.getGuild().getId()).getPrefix()).append("help <command>`");
        } catch (NullPointerException ex) {
            builder.append("Prefix for commands is `c!`\nView additional info using `c!help <command>`");
        }
        for (CommandCategory type : CommandCategory.getTypes()) {
            if (type == CommandCategory.MUSIC || type == CommandCategory.BOT_OWNER || type == CommandCategory.HIDDEN) { // Neexistujici kategorie (zatim)
                return builder;
            }
            builder.append("\n\n");
            builder.append(type.getEmote()).append(" | **").append(type.formattedName()).append("** - ").append(cm.getCommandsByCategory(type).size()).append("\n");
            for (FinalCommand c : cm.getCommands()) {
                if (c.getCommandCategory().equals(type)) {
                    builder.append("`").append(c.getName()).append("` ");
                }
            }
        }
        return builder;
    }

}
