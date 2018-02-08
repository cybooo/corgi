package cz.wake.corgibot.runnable;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.sql.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.TimerTask;

public class ReminderTask extends TimerTask {

    private final CorgiBot plugin;
    private final ConnectionPoolManager pool;
    private long reminderTime;
    private String userId, message;

    public ReminderTask(CorgiBot plugin){
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin, "Reminder-Pool");
    }

    @Override
    public void run() {

        long now = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM corgibot.reminders;");
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                reminderTime = ps.getResultSet().getLong("remind_time");
                if (reminderTime < now) {
                    userId = ps.getResultSet().getString("user_id");
                    message = ps.getResultSet().getString("reminder");
                    try {
                        CorgiBot.getJda().getUserById(userId).openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        CorgiBot.getInstance().getSql().deleteReminder(userId, reminderTime);
    }
}
