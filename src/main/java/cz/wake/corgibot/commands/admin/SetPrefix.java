package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

@SinceCorgi(version = "1.2.0")
public class SetPrefix implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                CorgiBot.getPrefixes().set(message.getGuild().getId(), ".");
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Prefix byl vyresetován zpět na `.`").build()).queue();
            } else if (args[0].length() < 3) {
                CorgiBot.getPrefixes().set(message.getGuild().getId(), args[0]);
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format("Prefix byl nastaven na `%s`", args[0])).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Nelze nastavit prefix, který má víc než tři znaky!", channel);
                return;
            }
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setDescription(String.format("Aktuální prefix pro server je `%s`", CorgiBot.getPrefix(message.getGuild().getId()))).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "prefix";
    }

    @Override
    public String getDescription() {
        return "Nastavení vlastního prefixu pro server.";
    }

    @Override
    public String getHelp() {
        return "%prefix reset/[prefix]";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTARTOR;
    }

    @Override
    public Rank getRank() {
        return Rank.ADMINISTRATOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"setprefix"};
    }
}
