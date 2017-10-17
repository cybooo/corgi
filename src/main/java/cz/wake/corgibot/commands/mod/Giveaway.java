package cz.wake.corgibot.commands.mod;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class Giveaway implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String str = message.getRawContent().substring(9).trim();
        String[] parts = str.split("\\s+", 2);
        try {
            int sec = Integer.parseInt(parts[0]);
            if(sec < 30){
                message.delete().queue();
                MessageUtils.sendAutoDeletedMessage("Čas giveawaye je příliš krátký, nejkratší možný čas je 30s", 20000, channel);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.addReaction("\uD83C\uDF89").queue();
                new cz.wake.corgibot.managers.Giveaway(sec, m, parts.length > 1 ? parts[1] : null).start();
            });
            message.delete().queue();
        } catch (NumberFormatException ex) {
            MessageUtils.sendAutoDeletedMessage("Nelze zadat vteřiny v tomto tvaru `" + parts[0] + "`", 15000, channel);
        } catch (Exception em){
            CorgiBot.LOGGER.error("Chyba při provádení příkazu .giveaway!", em);
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
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}

