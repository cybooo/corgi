package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

@SinceCorgi(version = "0.5")
public class Giveaway implements Command {


    //TODO: Kompletně předělat...

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        try {
            int sec = Integer.parseInt(args[0]);
            if (sec < 30) {
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage("Čas giveawaye je příliš krátký, nejkratší možný čas je 30s", 20000, channel);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.addReaction("\uD83C\uDF89").queue();
                new cz.wake.corgibot.managers.Giveaway(sec, m, args.length > 1 ? args[1] : null).start();
            });
            message.delete().queue();
        } catch (NumberFormatException ex) {
            MessageUtils.sendAutoDeletedMessage("Nelze zadat vteřiny v tomto tvaru `" + args[0] + "`", 15000, channel);
        } catch (Exception em) {
            CorgiBot.LOGGER.error("Chyba při provádení příkazu " + gw.getPrefix() + "giveaway!", em);
        }
    }

    @Override
    public String getCommand() {
        return "giveaway";
    }

    @Override
    public String getDescription() {
        return "Chceš pořádat na serveru Giveaway? Tímto příkazem ho vytvoříš snadno!\nStačí pouze zaktivovat a počkat si na výherce!";
    }

    @Override
    public String getHelp() {
        return "%giveaway <čas> [výhra]";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }
}

