package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.TemporaryReminder;
import cz.wake.corgibot.sql.ConnectionPoolManager;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.TimerTask;

public class ReminderTask extends TimerTask {

    private final CorgiBot plugin;

    public ReminderTask(CorgiBot plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        HashSet<TemporaryReminder> reminders = plugin.getSql().getAllReminders();
        try {
            reminders.forEach(reminder -> {
                if(reminder.getDate() < now){
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
