package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.Giveaway2;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.TimeUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = "giveaway",
        aliases = {"gw"},
        help = "commands.giveaway.help",
        description = "commands.giveaway.description",
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

    private static Period getTimeFromInput(String input, MessageChannel channel, GuildWrapper gw) {
        try {
            String inputFinal = input
                    .replace(I18n.getLoc(gw, "commands.giveaway.days"), "d")
                    .replace(I18n.getLoc(gw, "commands.giveaway.hours"), "h")
                    .replace(I18n.getLoc(gw, "commands.giveaway.minutes"), "m")
                    .replace(I18n.getLoc(gw, "commands.giveaway.seconds"), "s");
            return periodParser.parsePeriod(inputFinal);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.giveaway.invalid-time-format"),
                    channel);
            return null;
        }
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        /**
         * c!giveaway 1h30m | Cookie | 2 | :smile: | #ffffff
         * c!giveaway list
         */

        // new String("😄");

        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GIVEAWAY_BLUE)
                    .setTitle(I18n.getLoc(gw, "commands.giveaway.embed-title"))
                    .setDescription(I18n.getLoc(gw, "commands.giveaway.embed-description"))
                    .addField(I18n.getLoc(gw, "commands.giveaway.field-1-name"), I18n.getLoc(gw, "commands.giveaway.field-1-description").replace("{%}", gw.getPrefix()), false)
                    .addField(I18n.getLoc(gw, "commands.giveaway.field-2-name"), I18n.getLoc(gw, "commands.giveaway.field-2-description").replace("{%}", gw.getPrefix()), false)
                    .addField(I18n.getLoc(gw, "commands.giveaway.field-3-name"), I18n.getLoc(gw, "commands.giveaway.field-3-description"), false).build()).queue();
        } else {
            if (args[0].equalsIgnoreCase("list")) {

                PagedTableBuilder pb = new PagedTableBuilder();
                pb.addColumn(I18n.getLoc(gw, "commands.giveaway.id"));
                pb.addColumn(I18n.getLoc(gw, "commands.giveaway.prize"));
                pb.addColumn(I18n.getLoc(gw, "commands.giveaway.amount-of-winners"));
                pb.addColumn(I18n.getLoc(gw, "commands.giveaway.ends-in"));

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
                        MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.giveaway.invalid-amount-of-winners"), channel);
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
                }

                Period p = getTimeFromInput(time, channel, gw);
                DateTime start = new DateTime();  //NOW
                DateTime end = start.plus(p);
                long kekTime = end.getMillis() - start.getMillis();

                if (kekTime < 150000) {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.giveaway.giveaway-3-minutes"), channel);
                    message.delete().queue();
                    return;
                }

                int finalWinners = winners;
                String finalPrize = prize;
                String finalEmoji = emoji != null ? emoji : "🎉";
                String finalColor = color;
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setDescription(I18n.getLoc(gw, "commands.giveaway.generating")).build()).queue(m -> {
                    // Value "1000727596788633610>" is not snowflake.
                    // Still did not figure out why the > stays there, so i'll just replace it myself. Fell free to PR my stupidity.
                    m.addReaction(Emoji.fromUnicode(finalEmoji.replace(">", ""))).queue();
                    new Giveaway2(gw, m, end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor).start();
                    CorgiBot.getInstance().getSql().registerGiveawayInSQL(member.getGuild().getId(), channel.getId(), m.getId(), start.getMillis(), end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor);
                });
                message.delete().reason(I18n.getLoc(gw, "commands.giveaway.message-delete-reason")).queue();
            }
        }
    }
}

