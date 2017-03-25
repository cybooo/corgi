package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GiveawayCommand extends ListenerAdapter {

    //Multicommand

    @Override
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
                event.getChannel().sendMessage(":tada:  **GIVEAWAY!**  :tada:\n" + (parts.length > 1 ? "\u25AB*`" + parts[1] + "`*\u25AB\n" : "") + "Klikni na \uD83C\uDF89 ke vstupu!").queue(m -> {
                    m.addReaction("\uD83C\uDF89").queue();
                    new Giveaway(sec, m, parts.length > 1 ? parts[1] : null).start();
                });
                event.getMessage().delete().queue();
            } catch (NumberFormatException ex) {
                MessageUtils.sendErrorMessage("Nelze zadat vteřiny v tomto tvaru `" + parts[0] + "`", event.getChannel());
            }
        } else if (event.getMessage().getRawContent().startsWith(".greroll")) {
            if (!PermissionUtil.checkPermission(event.getGuild(), event.getMember(), Permission.MANAGE_SERVER)) {
                MessageUtils.sendErrorMessage("Musíš být správce tohoto serveru!", event.getChannel());
                return;
            }
            String id = event.getMessage().getRawContent().substring(8).trim();
            if (!id.matches("\\d{17,22}")) {
                MessageUtils.sendErrorMessage("Neplatná zpráva", event.getChannel());
                return;
            }
            Message m = event.getChannel().getMessageById(id).complete();
            if (m == null) {
                MessageUtils.sendErrorMessage("Zpráva nenalezena!", event.getChannel());
                return;
            }
            m.getReactions()
                    .stream().filter(mr -> mr.getEmote().getName().equals("\uD83C\uDF89"))
                    .findAny().ifPresent(mr -> {
                List<User> users = new LinkedList<>(mr.getUsers().complete());
                users.remove(m.getJDA().getSelfUser());
                String uid = users.get((int) (Math.random() * users.size())).getId();
                event.getChannel().sendMessage("Gratulujeme <@" + id + ">! Vyhrál jsi Giveaway!").queue();
            });
        }
    }
}

