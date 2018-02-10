package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.utils.*;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.HashSet;

@SinceCorgi(version = "1.3.0")
public class Reminder implements ICommand {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k reminderu").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].contains("list")){
            HashSet<TemporaryReminder> list = CorgiBot.getInstance().getSql().getRemindersByUser(sender.getId());

            if(list.isEmpty()){
                MessageUtils.sendErrorMessage("Nemáš nastavené žádné upozornění!", channel);
                return;
            }

            StringBuilder mess = new StringBuilder();
            mess.append(EmoteList.ALARM_CLOCK + " | **Seznam budoucích upozornění pro " + sender.getName() + "**:");
            mess.append("\n```markdown\n");
            mess.append("# ID | ZBÝVAJÍCÍ ČAS | TEXT\n\n");

            for (TemporaryReminder tr : list){
                mess.append(tr.getReminderId() + " | " + TimeUtils.toShortTime(tr.getDate() - System.currentTimeMillis()) + " | " + tr.getMessage() + "\n");
            }

            mess.append("```");
            channel.sendMessage(mess.toString()).queue();
        } else if (args[0].contains("delete")){
            if(args.length == 1){
                MessageUtils.sendErrorMessage("Nezadal jsi ID. Zkus to znova!", channel);
                return;
            }
            String id = args[1];
            if(id == null){
                MessageUtils.sendErrorMessage("Nezadal jsi ID. Zkus to znova!", channel);
                return;
            }
            if(FormatUtil.isStringInt(id)){
                int convertedId = Integer.valueOf(id);
                try {
                    CorgiBot.getInstance().getSql().deleteReminderById(sender.getId(), convertedId);
                    channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Upozornění s ID **" + convertedId + "** bylo smazáno!").build()).queue();
                } catch (Exception e){
                    MessageUtils.sendErrorMessage("Zadané ID neexistuje nebo se jedná o interní chybu!", channel);
                }
            } else {
                MessageUtils.sendErrorMessage("Zadané ID není číslo!", channel);
            }
        } else {

            String request = message.getContentRaw().replace("rm", "").replace("remindme", "").replace("reminder", "").replace("upozorneni", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\|");

            // .reminder 2d2h7m | Vypnout Corgiho!

            // 1 parametr?
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage("Něco ti tam zřejmě chybí! Příklad: %reminder 2h | Vypnout Corgiho!".replace("%", gw.getPrefix()), channel);
                return;
            }

            // Delsi jak 1000 znaku
            if (arguments[1].length() > 1000) {
                MessageUtils.sendErrorMessage("Promiň, ale upozornění delší jak 1000 znaků nelze zpracovat! Tvoje žádané upozornění má délku - %d".replace("%d", String.valueOf(arguments[1].length())), channel);
                return;
            }

            String time = arguments[0].replaceAll("\\s+", "");
            String reminderMessage = arguments[1].substring(1);

            Period p = getTimeFromInput(time, channel);

            DateTime start = new DateTime();  //NOW
            DateTime end = start.plus(p);

            long millis = end.getMillis() - start.getMillis(); // Rozdil na upozorneni

            if (millis < 300000L) {
                MessageUtils.sendErrorMessage("Minimální čas na upozornění je 5 minut!", channel);
                return;
            }

            try {
                // SQL
                CorgiBot.getInstance().getSql().addReminder(sender.getId(), end.getMillis(), reminderMessage);

            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.sendErrorMessage("Interní chyba při provádění operace, zkus to zachvilku!", channel);
                return;
            }

            // Oznameni
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.ALARM_CLOCK + " | Jistě! Upozorním tě za **" + TimeUtils.toYYYYHHmmssS(millis) + "**").build()).queue();
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
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
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
