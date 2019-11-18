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
                channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Zkontroluj si zprávy")
                        .setDescription(EmoteList.MAILBOX + " | Odeslal jsem ti do zpráv nápovědu s příkazy!").build()).queue();
            }
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE)
                    .setAuthor("Nápověda k CorgiBot", null, channel.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(getContext(member, message.getGuild())).setFooter("Podrobnější popis nalezneš na: https://corgibot.xyz/prikazy", null)
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
                channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - " + commandName + " :question:")
                        .setDescription(c.getDescription() + "\n\n**Použití**\n" + c.getHelp().replace("%", gw.getPrefix()))
                        .setColor(Constants.LIGHT_BLUE).setFooter("Aliasy: " + String.join(", ", c.getAliases()), null).build()).queue();
            });
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
        return "%help - Odešle do tvých zpráv seznam příkazů\n" +
                "%help [příkaz] - Zobrazí informace o příkazu a jeho použití.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pomoc", "prikazy", "commands"};
    }

    private StringBuilder getContext(Member member, Guild guild) {
        StringBuilder builder = new StringBuilder();
        CommandHandler ch = new CommandHandler();
        try {
            builder.append("Prefix pro příkazy na " + guild.getName() + " je `" + BotManager.getCustomGuild(member.getGuild().getId()).getPrefix() + "`\nDodatečné informace o příkazu `" + BotManager.getCustomGuild(member.getGuild().getId()).getPrefix() + "help <příkaz>`");
        } catch (NullPointerException ex){
            builder.append("Prefix pro příkazy je `c!`\nDodatečné informace zobrazíš pomocí `c!help <příkaz>`");
        }
        for (CommandCategory type : CommandCategory.getTypes()) {
            if (type == CommandCategory.MUSIC || type == CommandCategory.BOT_OWNER || type == CommandCategory.HIDDEN) { // Neexistujici kategorie (zatim)
                return builder;
            }
            builder.append("\n\n");
            builder.append(type.getEmote() + " | **" + type.formattedName() + "** - " + ch.getCommandsByType(type).size() + "\n");
            for (Command c : ch.getCommands()) {
                if (c.getCategory().equals(type)) {
                    builder.append("`" + c.getCommand() + "` ");
                }
            }
        }
        return builder;
    }

}
