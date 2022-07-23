package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.utils.*;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@CommandInfo(
        name = "reminder",
        aliases = {"remindme", "rm"},
        description = "commands.reminder.description",
        help = "commands.reminder.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.3.0")
public class Reminder implements CommandBase {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();

    private static Period getTimeFromInput(String input, MessageChannel channel) {
        try {
            return periodParser.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendErrorMessage("Invalid time format! Try: `1d` -> for 1 day.",
                    channel);
            return null;
        }
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY)
                    .setTitle(I18n.getLoc(gw, "internal.general.help-command") + " - reminder").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].contains("list")) {
            HashSet<TemporaryReminder> list = CorgiBot.getInstance().getSql().getRemindersByUser(member.getUser().getId());

            if (list.isEmpty()) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.no-reminder"), channel);
                return;
            }

            //NEW
            PagedTableBuilder tb = new PagedTableBuilder();
            tb.addColumn(I18n.getLoc(gw, "commands.reminder.table-id"));
            tb.addColumn(I18n.getLoc(gw, "commands.reminder.table-time"));
            tb.addColumn(I18n.getLoc(gw, "commands.reminder.table-text"));

            for (TemporaryReminder tr : list) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(tr.getReminderId()));
                row.add(TimeUtils.toShortTime(tr.getDate() - System.currentTimeMillis()));
                row.add(tr.getMessage());
                tb.addRow(row);
            }

            PaginationUtil.sendPagedMessage(channel, tb.build(), 0, message.getAuthor(), "kek");

        } else if (args[0].contains("delete")) {
            if (args.length == 1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.no-id"), channel);
                return;
            }
            String id = args[1];
            if (id == null) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.no-id"), channel);
                return;
            }
            if (FormatUtil.isStringInt(id)) {
                int convertedId = Integer.parseInt(id);
                try {
                    CorgiBot.getInstance().getSql().deleteReminderById(member.getUser().getId(), convertedId);
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN)
                            .setDescription(I18n.getLoc(gw, "commands.reminder.deleted-reminder")
                                    .replace("{1}", String.valueOf(convertedId))).build()).queue();
                } catch (Exception e) {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.non-existing-id"), channel);
                }
            } else {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.id-is-not-number"), channel);
            }
        } else {

            String request = message.getContentRaw().replace("rm", "")
                    .replace("remindme", "").replace("reminder", "");
            String[] arguments = request.split("\\;");

            // .reminder 2d2h7m ; Shutdown corgi!

            // 1 parameter?
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.incorrect-command").replace("%", gw.getPrefix()), channel);
                return;
            }

            // Longer than 1000 characters
            if (arguments[1].length() > 1000) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.too-long-time").replace("{1}", String.valueOf(arguments[1].length())), channel);
                return;
            }

            String time = arguments[0].replaceAll("\\s+", "").replace(gw.getPrefix(), "");
            String reminderMessage = arguments[1].substring(1);

            Period p = getTimeFromInput(time, channel);
            DateTime start = new DateTime();  //NOW
            DateTime end = start.plus(p);

            long millis = end.getMillis() - start.getMillis();

            if (millis < 60000L) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.minimal-time"), channel);
                return;
            }

            try {
                // SQL
                CorgiBot.getInstance().getSql().addReminder(member.getUser().getId(), end.getMillis(), reminderMessage);
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.command-failed"), channel);
                return;
            }

            // Reminder
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN)
                    .setDescription(EmoteList.ALARM_CLOCK + " | " + I18n.getLoc(gw, "commands.reminder.final-message")
                            .replace("{1}", TimeUtils.toYYYYHHmmssS(millis))).build()).queue();
        }
    }
}
