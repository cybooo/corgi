package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;

import java.util.HashSet;

public record ReminderTask(CorgiBot plugin) implements Runnable {

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        HashSet<TemporaryReminder> reminders = plugin.getSql().getAllReminders();
        try {
            reminders.forEach(reminder -> {
                if (reminder.getDate() < now) {
                    MessageUtils.sendPrivateMessage(CorgiBot.getJda().getUserById(reminder.getUserId()), reminder.getMessage());
                    CorgiBot.getInstance().getSql().deleteReminder(reminder.getUserId(), reminder.getDate());
                }
            });
        } catch (Exception e) {
            CorgiLogger.fatalMessage("Error when Corgi checked reminders:\n");
            e.printStackTrace();
        }
    }
}
