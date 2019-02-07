package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.utils.*;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SinceCorgi(version = "1.3.0")
public class Reminder implements Command {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY)
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
                int convertedId = Integer.valueOf(id);
                try {
                    CorgiBot.getInstance().getSql().deleteReminderById(member.getUser().getId(), convertedId);
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
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
                    .replace("remindme", "").replace("reminder", "")
                    .replace("upozorneni", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\|");

            // .reminder 2d2h7m | Vypnout Corgiho!

            // 1 parametr?
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.incorrect-command").replace("%", gw.getPrefix()), channel);
                return;
            }

            // Delsi jak 1000 znaku
            if (arguments[1].length() > 1000) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.reminder.too-long-time").replace("{1}", String.valueOf(arguments[1].length())), channel);
                return;
            }

            String time = arguments[0].replaceAll("\\s+", "");
            String reminderMessage = arguments[1].substring(1);

            Period p = getTimeFromInput(time, channel);

            DateTime start = new DateTime();  //NOW
            DateTime end = start.plus(p);

            long millis = end.getMillis() - start.getMillis(); // Rozdil na upozorneni

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

            // Oznameni
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                    .setDescription(EmoteList.ALARM_CLOCK + " | " + I18n.getLoc(gw, "commands.reminder.final-message")
                            .replace("{1}", TimeUtils.toYYYYHHmmssS(millis))).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "reminder";
    }

    @Override
    public String getDescription() {
        return "Zapomínáš na všechno? Tak si nastav oznámení!\n" +
                "Pomocí jednoho příkazu si nastavíš, kdy tě má Corgi upozornit!" +
                "Upozornění jsou přesné v rozmezí 30 vteřin!";
    }

    @Override
    public String getHelp() {
        return "%reminder - Zobrazí nápovědu\n" +
                "%reminder [čas] | [text] - Nastavení upozornění\n" +
                "%reminder list - Zobrazení všech tvých budoucích upozorenění\n" +
                "%reminder delete [ID] - Smaže upozornění";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"upozorneni", "remindme", "rm"};
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
