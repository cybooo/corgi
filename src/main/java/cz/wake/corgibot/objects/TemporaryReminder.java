package cz.wake.corgibot.objects;

public record TemporaryReminder(int reminderId, String userId, long date, String message) {

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
        if (message.length() > 100) {
            return message.substring(0, 100).concat("...");
        }
        return message;
    }
}
