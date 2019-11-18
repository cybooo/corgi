package cz.wake.corgibot.objects;

public class TemporaryReminder {

    private int reminderId;
    private String userId;
    private long date;
    private String message;

    public TemporaryReminder(int reminderId, String userId, long date, String message) {
        this.reminderId = reminderId;
        this.userId = userId;
        this.date = date;
        this.message = message;
    }

    public int getReminderId() {
        return reminderId;
    }

    public String getUserId() {
        return userId;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        if(message.length() > 100){
            return message.substring(0, 100).concat("...");
        }
        return message;
    }
}
