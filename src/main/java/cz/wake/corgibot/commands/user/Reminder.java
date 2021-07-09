package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.utils.*;
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
        description = "Forgetting everything! Set a reminder!\n" +
                "With only one command, you can set when Corgi should remind you!" +
                "Alerts are accurate within 30 seconds!",
        help = "%reminder - Show help\n" +
                "%reminder [time] ; [text] - Set a reminder\n" +
                "%reminder list - Show all your reminders\n" +
                "%reminder delete [ID] - Deletes a reminder",
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
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY)
                    .setTitle("Help" + " - reminder").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].contains("list")) {
            HashSet<TemporaryReminder> list = CorgiBot.getInstance().getSql().getRemindersByUser(member.getUser().getId());

            if (list.isEmpty()) {
                MessageUtils.sendErrorMessage("You don't have any reminders!", channel);
                return;
            }

            //NEW
            PagedTableBuilder tb = new PagedTableBuilder();
            tb.addColumn("ID");
            tb.addColumn("Remaining time");
            tb.addColumn("Reminder text");

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
                MessageUtils.sendErrorMessage("You did not provide any ID, try again!", channel);
                return;
            }
            String id = args[1];
            if (id == null) {
                MessageUtils.sendErrorMessage("You did not provide any ID, try again!", channel);
                return;
            }
            if (FormatUtil.isStringInt(id)) {
                int convertedId = Integer.parseInt(id);
                try {
                    CorgiBot.getInstance().getSql().deleteReminderById(member.getUser().getId(), convertedId);
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                            .setDescription("Reminder with ID **{1}** was deleted!"
                                    .replace("{1}", String.valueOf(convertedId))).build()).queue();
                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("ID does not exist, or something went wrong!", channel);
                }
            } else {
                MessageUtils.sendErrorMessage("Provided ID is not a number!", channel);
            }
        } else {

            String request = message.getContentRaw().replace("rm", "")
                    .replace("remindme", "").replace("reminder", "");
            String[] arguments = request.split("\\;");

            // .reminder 2d2h7m ; Vypnout Corgiho!

            // 1 parametr?
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage("Incorrectly executed command! Example: %reminder 2h ; Restart Corgi!".replace("%", gw.getPrefix()), channel);
                return;
            }

            // Delsi jak 1000 znaku
            if (arguments[1].length() > 1000) {
                MessageUtils.sendErrorMessage("Sorry, reminders longer than 1000 characters cannot be processed! Lenght of your reminder: {1}".replace("{1}", String.valueOf(arguments[1].length())), channel);
                return;
            }

            String time = arguments[0].replaceAll("\\s+", "").replace(gw.getPrefix(), "");
            String reminderMessage = arguments[1].substring(1);

            Period p = getTimeFromInput(time, channel);
            DateTime start = new DateTime();  //NOW
            DateTime end = start.plus(p);

            long millis = end.getMillis() - start.getMillis(); // Rozdil na upozorneni

            if (millis < 60000L) {
                MessageUtils.sendErrorMessage("Minimum time is 1 minte!", channel);
                return;
            }

            try {
                // SQL
                CorgiBot.getInstance().getSql().addReminder(member.getUser().getId(), end.getMillis(), reminderMessage);
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.sendErrorMessage("Incorrectly executed command! Example: %reminder 2h ; Restart Corgi!", channel);
                return;
            }

            // Oznameni
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                    .setDescription(EmoteList.ALARM_CLOCK + " | " + "Sure! I'll remind you in {1}"
                            .replace("{1}", TimeUtils.toYYYYHHmmssS(millis))).build()).queue();
        }
    }
}
