package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandHandler;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;

@SinceCorgi(version = "0.1")
public class Help implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            if (channel.getType() == ChannelType.TEXT) {
                channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Zkontroluj si zprávy")
                        .setDescription(EmoteList.MAILBOX + " | Odeslal jsem ti do zpráv nápovědu s příkazy!").build()).queue();
            }
            sender.openPrivateChannel().queue(msg -> {
                msg.sendMessage(MessageUtils.getEmbed(Constants.BLUE)
                        .setAuthor("Nápověda k CorgiBot", null, channel.getJDA().getSelfUser().getAvatarUrl())
                        .setDescription(getContext(member)).setFooter("Podrobnější popis nalezneš na: https://corgibot.xyz/prikazy", null)
                        .build()).queue();
            });
        } else {
            String commandName = args[0];
            CommandHandler ch = new CommandHandler();
            for (ICommand c : ch.getCommands()) {
                if (c.getCommand().equalsIgnoreCase(commandName)) { //Normal
                    channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - " + commandName + " :question:")
                            .setDescription(c.getDescription() + "\n\n**Použití**\n" + c.getHelp().replace("%", gw.getPrefix())).build()).queue();
                }
            }
        }
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Základní nápověda pro Corgiho.";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pomoc", "prikazy", "commands"};
    }

    private StringBuilder getContext(Member member) {
        StringBuilder builder = new StringBuilder();
        CommandHandler ch = new CommandHandler();
        builder.append("Prefix pro příkazy na tvém serveru je `" + BotManager.getCustomGuild(member.getGuild().getId()).getPrefix() + "`\nDodatečné informace o příkazu `" + BotManager.getCustomGuild(member.getGuild().getId()).getPrefix() + "help <příkaz>`");
        for (CommandType type : CommandType.getTypes()) {
            if (type == CommandType.MUSIC || type == CommandType.BOT_OWNER) { // Neexistujici kategorie (zatim)
                return builder.append("");
            }
            builder.append("\n\n");
            builder.append(type.getEmote() + " | **" + type.formattedName() + "** - " + ch.getCommandsByType(type).size() + "\n");
            for (ICommand c : ch.getCommands()) {
                if (c.getType().equals(type)) {
                    builder.append("`" + c.getCommand() + "` ");
                }
            }
        }
        return builder;
    }

}
