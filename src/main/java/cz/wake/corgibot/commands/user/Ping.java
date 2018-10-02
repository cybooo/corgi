package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.CommandEvent;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;

public class Ping extends Command {

    private static boolean running = false;

    public Ping() {
        this.name = "ping";
        this.description = "Zjištění rychlosti odezvy.";
        this.category = new Category(CommandCategory.GENERAL);
        this.usage.add("ping");
    }

    @Override
    public void onExecuted(CommandEvent event) throws Throwable {
        if (!running) {
            running = true;
            event.getChannel().sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Vypočítávám ping...").build()).queue(m -> {
                int pings = 5;
                int lastResult;
                int sum = 0, min = 999, max = 0;
                long start = System.currentTimeMillis();
                for (int j = 0; j < pings; j++) {
                    m.editMessage(MessageUtils.getEmbed(Constants.ORANGE).setDescription(pingMessages[j % pingMessages.length]).build()).complete();
                    lastResult = (int) (System.currentTimeMillis() - start);
                    sum += lastResult;
                    min = Math.min(min, lastResult);
                    max = Math.max(max, lastResult);
                    try {
                        Thread.sleep(1_500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    start = System.currentTimeMillis();
                }
                m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format(EmoteList.LOUDSPEAKER + " | **Průměrný ping je:** %dms (min: %d, max: %d)", (int) Math.ceil(sum / 5f), min, max)).build()).complete();
                running = false;
            });
        } else {
            MessageUtils.sendErrorMessage("Aktuálně nelze zjistit ping, jelikož již probíhá sken. Zkus to zachvilku!", event.getChannel());
        }
    }

    private static final String[] pingMessages = new String[]{
            ":ping_pong::white_small_square::black_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::black_small_square::white_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
    };

}
