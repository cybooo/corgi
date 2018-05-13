package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.Giveaway2;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Arrays;

@SinceCorgi(version = "1.3.2")
public class Giveaway implements Command {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();


    //TODO: Kompletně předělat...

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        /**
         * c!giveaway 1h30m | Výhra v loterii | 2 | :smile: | #fffff
         * c!giveaway list
         * c!giveaway stop [ID]
         * c!giveaway reroll [MSG_ID]
         * c!giveaway builder
         */

        if(args.length < 1){
            //HELP
        } else {
            // Format message
            String request = message.getContentRaw().replaceAll("\\s+\\|", "|").replaceAll("\\|\\s+", "|").replaceAll("\\|", "|").replace("giveaway ", "").replace("gw ", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\|");

            // Time
            String time = arguments[0].replaceAll("\\s+", "");

            // Prize
            String prize = null;
            if(arguments.length >= 2){
                prize = arguments[1];
            }

            // Winners
            String maxWinners;
            int winners = 1;
            if(arguments.length >= 3){
                maxWinners = arguments[2].replaceAll("\\s+", "");
                if (FormatUtil.isStringInt(maxWinners)) {
                    winners = Integer.valueOf(maxWinners);
                } else {
                    MessageUtils.sendErrorMessage("Špatně zadaný počet výherců! Zkus to znova...", channel);
                    return;
                }
            }

            // Emoji
            String emoji = null;
            if(arguments.length >= 4){
                emoji = arguments[3].replaceAll("\\s+", "");
            }

            // Color
            String color = null;
            if(arguments.length >= 5){
                //TODO: Kontrola
                color = arguments[4].replaceAll("\\s+", "");
            }

            Period p = getTimeFromInput(time, channel);
            DateTime start = new DateTime();  //NOW
            DateTime end = start.plus(p);
            long milis = end.getMillis() - start.getMillis();

            int finalWinners = winners;
            String finalPrize = prize;
            String finalEmoji = emoji != null ? emoji : "\uD83C\uDF89";
            String finalColor = color;
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.addReaction(finalEmoji).queue();
                new Giveaway2(m, end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor).start();
                CorgiBot.getInstance().getSql().registerGiveawayInSQL(member.getGuild().getId(),channel.getId(), m.getId(), start.getMillis(),end.getMillis(),finalPrize,finalWinners,finalEmoji, finalColor);
            });

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
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }

    private static Period getTimeFromInput(String input, MessageChannel channel) {
        try {
            return periodParser.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendErrorMessage("Byl zadán neplatný formát času! Zkus to třeba takto `1d` -> pro 1 den.",
                    channel);
            return null;
        }
    }
}

