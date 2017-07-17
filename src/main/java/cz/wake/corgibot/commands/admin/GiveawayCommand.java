package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GiveawayCommand implements Command {

    //Multicommand

    /*@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getRawContent().equals(".ghelp")) {
            event.getChannel().sendMessage(MessageUtils.getEmbed(event.getAuthor(), Color.ORANGE)
                    .setDescription("Seznam příkazů na ovládání Giveawaye!\n**.ghelp** - Vyvolání tohoto příkazu" +
                            "\n**.gstart <vteřin> [vyhra]** - Start Giveawaye. Př. `.gstart 180` ke startu na 3 minuty" +
                            "\n**.greroll <messageId>** - manuální vyhodnocení, pokuď selžu :(").build()).queue();
        } else if (event.getMessage().getRawContent().startsWith(".gstart")) {
            if (!PermissionUtil.checkPermission(event.getGuild(), event.getMember(), Permission.MANAGE_SERVER)) {
                event.getChannel().sendMessage("Nemáš oprávnění na používání tohoto příkazu!").queue();
                return;
            }
            String str = event.getMessage().getRawContent().substring(7).trim();
            String[] parts = str.split("\\s+", 2);
            try {
                int sec = Integer.parseInt(parts[0]);
                event.getChannel().sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                    m.addReaction("\uD83C\uDF89").queue();
                    new Giveaway(sec, m, parts.length > 1 ? parts[1] : null).start();
                });
                event.getMessage().delete().queue();
            } catch (NumberFormatException ex) {
                MessageUtils.sendErrorMessage("Nelze zadat vteřiny v tomto tvaru `" + parts[0] + "`", event.getChannel());
            }
        }
    }*/

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String str = message.getRawContent().substring(9).trim();
        String[] parts = str.split("\\s+", 2);
        try {
            int sec = Integer.parseInt(parts[0]);
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.addReaction("\uD83C\uDF89").queue();
                new Giveaway(sec, m, parts.length > 1 ? parts[1] : null).start();
            });
            message.delete().queue();
        } catch (NumberFormatException ex) {
            MessageUtils.sendErrorMessage("Nelze zadat vteřiny v tomto tvaru `" + parts[0] + "`", channel);
        }
    }

    @Override
    public String getCommand() {
        return "giveaway";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}

