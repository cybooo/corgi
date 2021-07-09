package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "ping",
        description = "Get the bot ping.",
        help = "%ping",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.1")
public class Ping implements CommandBase {

    private static final String[] pingMessages = new String[]{
            ":ping_pong::white_small_square::black_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::black_small_square::white_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
    };
    private static boolean running = false;

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        if (!running) {
            running = true;
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Calculating ping ...").build()).queue(m -> {
                int pings = 5;
                int lastResult;
                int sum = 0, min = 999, max = 0;
                long start = System.currentTimeMillis();
                for (int j = 0; j < pings; j++) {
                    m.editMessage(MessageUtils.getEmbed(Constants.ORANGE).setDescription(pingMessages[j % pingMessages.length]).build()).queue();
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
                m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format(EmoteList.LOUDSPEAKER + " | **Average ping is:** %dms", CorgiBot.getJda().getGatewayPing())).build()).queue();
                running = false;
            });
        } else {
            MessageUtils.sendErrorMessage("Unable to detect ping at this time because a scan is already in progress. Try it for a moment!", channel);
        }
    }

}
