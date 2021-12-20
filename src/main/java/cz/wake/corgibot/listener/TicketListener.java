package cz.wake.corgibot.listener;

import cz.wake.corgibot.managers.TicketManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketListener extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getGuild() == null || event.getMember() == null) {
            return;
        }
//        switch (event.getComponentId()) {
//            case "openticket" -> {
//                TicketManager.createTicket(event.getGuild(), event.getMember());
//                event.deferEdit().queue();
//            }
//            case "closeticket" -> {
//                TicketManager.closeTicket(event.getTextChannel(), event.getMember());
//                event.deferEdit().queue();
//            }
//            case "reopenticket" -> {
//                TicketManager.reopenTicket(event.getTextChannel(), event.getMember());
//                event.deferEdit().queue();
//            }
//            case "deleteticket" -> event.getTextChannel().delete().queue();
//        }
    }
}
