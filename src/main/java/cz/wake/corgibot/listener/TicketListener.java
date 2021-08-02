package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.TicketManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class TicketListener extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        switch (event.getComponentId()) {
            case "openticket":
                TicketManager.createTicket(event.getGuild(), event.getMember());
                event.deferEdit().queue();
            case "closeticket":
                if (!Objects.equals(CorgiBot.getInstance().getSql().getTicketTranscriptCategory(event.getGuild().getId()), "0")) {

                }

        }

    }
}
