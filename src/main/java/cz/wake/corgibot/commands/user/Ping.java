package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "ping",
        description = "commands.ping.description",
        help = "commands.ping.help",
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
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setDescription(I18n.getLoc(gw, "commands.ping.calculating")).build()).queue(m -> {
                int pings = 5;
                int lastResult;
                int sum = 0, min = 999, max = 0;
                long start = System.currentTimeMillis();
                for (int j = 0; j < pings; j++) {
                    m.editMessageEmbeds(MessageUtils.getEmbed(Constants.ORANGE).setDescription(pingMessages[j % pingMessages.length]).build()).queue();
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
                m.editMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format(EmoteList.LOUDSPEAKER + I18n.getLoc(gw, "commands.ping.average-ping"), CorgiBot.getShardManager().getAverageGatewayPing())).build()).queue();
                running = false;
            });
        } else {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.ping.already-running"), channel);
        }
    }

}
