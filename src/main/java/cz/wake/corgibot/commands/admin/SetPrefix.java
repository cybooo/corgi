package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

@SinceCorgi(version = "1.2.0")
public class SetPrefix implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 1) {
            if (CorgiBot.isIsBeta()) {
                channel.sendMessage("Corgi je spuštěn v režimu BETA! Nelze upravovat prefix!").queue();
                return;
            }
            if (args[0].equalsIgnoreCase("reset")) {
                gw.setPrefix("c!", true);
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Prefix byl vyresetován zpět na `c!`").build()).queue();
            } else if (args[0].length() < 4) {
                gw.setPrefix(args[0], true);
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format("Prefix byl nastaven na `%s`", args[0])).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Nelze nastavit prefix, který má víc než tři znaky!", channel);
            }
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setDescription(String.format("Aktuální prefix pro server je `%s`", gw.getPrefix())).build()).queue();
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
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTARTOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"setprefix"};
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }
}
