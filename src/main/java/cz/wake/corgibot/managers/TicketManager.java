package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class TicketManager {

    public static void createTicket(Guild guild, Member author) {

        int openedTickets = 0;

        // TODO: figure out a better way + add maximum user tickets (SQL Storing?)
        for (TextChannel textChannel : guild.getTextChannels()) {
            if (textChannel.getName().toLowerCase().contains("ticket-")) {
                openedTickets++;
            }
        }

        if (openedTickets >= CorgiBot.getInstance().getSql().getMaximumTickets(guild.getId())) {
            MessageUtils.sendPrivateMessage(author.getUser(), "You can't currently create any more tickets!");
            return;
        }

        CorgiBot.getInstance().getSql().setOpenedTickets(guild.getId(), CorgiBot.getInstance().getSql().getOpenedTickets(guild.getId()) + 1);
        String categoryId = CorgiBot.getInstance().getSql().getTicketOpenedCategory(guild.getId());
        guild.createTextChannel("ticket-" + CorgiBot.getInstance().getSql().getOpenedTickets(guild.getId()), categoryId == null || categoryId.equals("0") ? null : guild.getCategoryById(categoryId)).queue(textChannel -> {
            // TODO: Make customizable (Webpanel?)

            textChannel.createPermissionOverride(guild.getPublicRole()).setDeny(
                    Permission.VIEW_CHANNEL
            ).queue();

            textChannel.createPermissionOverride(guild.getSelfMember()).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_SEND,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();

            textChannel.createPermissionOverride(author).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_SEND,
                    Permission.MESSAGE_HISTORY,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EXT_EMOJI
            ).queue();


            for (Role role : CorgiBot.getInstance().getSql().getTicketStaffRoles(guild.getId())) {
                textChannel.createPermissionOverride(role).setAllow(
                        Permission.VIEW_CHANNEL,
                        Permission.MESSAGE_SEND,
                        Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.MESSAGE_EXT_EMOJI
                ).queue();
            }

            textChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Ticket - " + author.getUser().getAsTag())
                    .setDescription("Click " + EmoteList.LOCK + " to close this ticket!")
                    .setColor(Constants.BLUE)
                    .build())
                    .setActionRow(Button.primary("closeticket", EmoteList.LOCK + " Close ticket"))
                    .queue();
        });
    }

    public static void closeTicket(TextChannel textChannel, Member closedBy) {
        textChannel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Constants.BLUE)
                .setTitle("Ticket closed")
                .setDescription("Ticket closed by " + closedBy.getAsMention())
                .build())
                .setActionRow(Button.primary("deleteticket", EmoteList.NO_ENTRY + " Delete ticket"), Button.primary("reopenticket", EmoteList.MAILBOX + " Open ticket"))
                .queue();


        textChannel.getPermissionOverride(textChannel.getGuild().getPublicRole()).getManager().deny(Permission.MESSAGE_SEND).queue();

        for (Role role : CorgiBot.getInstance().getSql().getTicketStaffRoles(textChannel.getGuild().getId())) {
            textChannel.getPermissionOverride(role).getManager().setAllow(
                    Permission.MESSAGE_SEND
            ).queue();
        }
    }

    public static void reopenTicket(TextChannel textChannel, Member openedBy) {
        textChannel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Constants.BLUE)
                        .setTitle("Ticket opened")
                        .setDescription("Ticket opened by " + openedBy.getAsMention())
                        .build())
                .setActionRow(Button.primary("closeticket", EmoteList.LOCK + " Close ticket"))
                .queue();

        for (PermissionOverride permissionOverride : textChannel.getPermissionOverrides()) {
            permissionOverride.getManager().setAllow(Permission.MESSAGE_SEND).queue();
        }
    }

    public static void generateTranscript(TextChannel textChannel) {

        // TODO: Generate HTML transcript based on transcript_template.html

    }

}
