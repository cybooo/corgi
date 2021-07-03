package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.Giveaway2;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.TimeUtils;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = "giveaway",
        aliases = {"gw"},
        help = "%giveaway 1h30m ; Discord Nitro ; 2 ; :smile: ; #ffffff\n\nFor a more detailed guide, use `%giveaway`",
        description = "Creates a giveaway.",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "2.3.2")
public class Giveaway implements CommandBase {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();


    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        /**
         * c!giveaway 1h30m | Výhra v loterii | 2 | :smile: | #ffffff
         * c!giveaway list
         */

        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GIVEAWAY_BLUE).setTitle("How to create a giveaway!")
                    .setDescription("A brief guide on how to create different Giveaways.")
                    .addField("Creating", "`{%}giveaway 30m` - Basic giveaway for 30 minutes.\n`{%}giveaway 1h ; FarCry 3` - Giveaway for 1 hour with the FarCry 3 prize.\n`{%}giveaway 2h ; Mafia 2 ; 5` - Giveaway for 2 hours with the Mafia 2 prize for 5 users.\n`{%}giveaway 1d3h ; Overwatch ; 1 ; \uD83D\uDE04` - Giveaway with a custom emoji (Only discord emojis)\n`{%}giveaway 4d ; CS:GO ; 3 ; \uD83D\uDE04 ; #ffffff` - Giveaway with a custom color".replace("{%}", gw.getPrefix()), false)
                    .addField("List all giveaways", "If multiple Giveaways is running on this server, you can view basic information using the following command: `{%}giveaway list`".replace("{%}", gw.getPrefix()), false)
                    .addField("Deleting a giveaway", "It's simple! Just delete the message that Corgi sent.".replace("{%}", gw.getPrefix()), false).setFooter("Corgi is saving everything! In case of Corgi going down, everything is gonna be saved.", null).build()).queue();
        } else {
            if (args[0].equalsIgnoreCase("list")) {

                PagedTableBuilder pb = new PagedTableBuilder();
                pb.addColumn("ID");
                pb.addColumn("Prize");
                pb.addColumn("Winners");
                pb.addColumn("Ends in");

                CorgiBot.getInstance().getSql().getAllGiveaways().forEach(g -> {
                    if (g.getGuildId().equals(message.getGuild().getId())) {
                        List<String> row = new ArrayList<>();
                        row.add(String.valueOf(g.getGiveawayId()));
                        row.add(g.getPrize());
                        row.add(String.valueOf(g.getMaxWinners()));
                        row.add(TimeUtils.toShortTime(g.getEndTime() - System.currentTimeMillis()));
                        pb.addRow(row);
                    }
                });

                PaginationUtil.sendPagedMessage(channel, pb.build(), 0, message.getAuthor(), "giveaway list");
            } else {
                // Format message
                String request = message.getContentRaw().replaceAll("\\s+\\;", ";").replaceAll("\\;\\s+", ";").replaceAll("\\;", ";").replace("giveaway ", "").replace("gw ", "").replace(gw.getPrefix(), "");
                String[] arguments = request.split("\\;");

                // Time
                String time = arguments[0].replaceAll("\\s+", "");

                // Prize
                String prize = null;
                if (arguments.length >= 2) {
                    prize = arguments[1];
                }

                // Winners
                String maxWinners;
                int winners = 1;
                if (arguments.length >= 3) {
                    maxWinners = arguments[2].replaceAll("\\s+", "");
                    if (FormatUtil.isStringInt(maxWinners)) {
                        winners = Integer.parseInt(maxWinners);
                    } else {
                        MessageUtils.sendErrorMessage("Invalid amount of winners! Try again..", channel);
                        return;
                    }
                }

                // Emoji
                String emoji = null;
                if (arguments.length >= 4) {
                    emoji = arguments[3].replaceAll("\\s+", "");
                }

                // Color
                String color = null;
                if (arguments.length >= 5) {
                    color = arguments[4].replaceAll("\\s+", "");
                    /*if (Pattern.compile("#?([A-Fa-f\\d]){6}").matcher(color).find()) {
                        color = color.startsWith("#") ? color : "#" + color;
                    } else {
                        MessageUtils.sendErrorMessage("Špatně zadaný formát barvy! Správný formát: #00000", channel);
                        return;
                    }*/
                }

                Period p = getTimeFromInput(time, channel);
                DateTime start = new DateTime();  //NOW
                DateTime end = start.plus(p);
                long kekTime = end.getMillis() - start.getMillis();

                if (kekTime < 150000) {
                    MessageUtils.sendErrorMessage("Giveaways can't be shorter than 3 minutes!", channel);
                    message.delete().queue();
                    return;
                }

                int finalWinners = winners;
                String finalPrize = prize;
                String finalEmoji = emoji != null ? emoji : "\uD83C\uDF89";
                String finalColor = color;
                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generating..").build()).queue(m -> {
                    m.addReaction(finalEmoji).queue();
                    new Giveaway2(m, end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor).start();
                    CorgiBot.getInstance().getSql().registerGiveawayInSQL(member.getGuild().getId(), channel.getId(), m.getId(), start.getMillis(), end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor);
                });
                message.delete().reason("Starting giveaway").queue();
            }
        }
    }

    private static Period getTimeFromInput(String input, MessageChannel channel) {
        try {
            return periodParser.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendErrorMessage("Invalid time format! Try this: `1d` -> for one day.",
                    channel);
            return null;
        }
    }
}

