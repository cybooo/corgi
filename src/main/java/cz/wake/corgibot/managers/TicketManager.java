package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.Ticket;
import cz.wake.corgibot.sql.SQLManager;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashSet;

public class TicketManager {

    private static final HashSet<Ticket> tickets = new HashSet<>();

    public static void createTicket(Guild guild, Member author) {
        String categoryId = CorgiBot.getInstance().getSql().getTicketOpenedCategory(guild.getId());
        guild.createTextChannel("ticket-" + CorgiBot.getInstance().getSql().getOpenedTickets(guild.getId()), categoryId == null || categoryId.equals("0") ? null : guild.getCategoryById(categoryId)).queue(textChannel -> {
            // TODO: Make customizable (Webpanel?)

            textChannel.createPermissionOverride(guild.getPublicRole()).setDeny(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_READ
            ).queue();

            textChannel.createPermissionOverride(guild.getSelfMember()).setAllow(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();

            textChannel.createPermissionOverride(author).setAllow(
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_READ,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();


            for (Role role : CorgiBot.getInstance().getSql().getTicketStaffRoles(guild.getId())) {
                textChannel.createPermissionOverride(role).setAllow(
                        Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.MESSAGE_EXT_EMOJI
                ).queue();
            }

            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Ticket - " + author.getUser().getAsTag())
                    .setDescription("Click :envelope_with_arrow: to close this ticket!")
                    .setColor(Constants.BLUE)
                    .build())
                    .setActionRow(Button.primary("closeticket", EmoteList.LOCK + " Close ticket"))
                    .queue();
        });
        CorgiBot.getInstance().getSql().setOpenedTickets(guild.getId(), CorgiBot.getInstance().getSql().getOpenedTickets(guild.getId()) + 1);
    }

    public static HashSet<Ticket> getTickets() {
        return tickets;
    }
}
