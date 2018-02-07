package cz.wake.corgibot.objects;

public class TemporaryReminder {

    private int reminderId;
    private long date;
    private String message;

    public TemporaryReminder(int reminderId, long date, String message) {
        this.reminderId = reminderId;
        this.date = date;
        this.message = message;
    }

    public int getReminderId() {
        return reminderId;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        if(message.length() > 15){
            return message.substring(0, 15).concat("...");
        }
        return message;
    }
}
