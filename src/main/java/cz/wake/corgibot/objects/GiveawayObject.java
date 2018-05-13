package cz.wake.corgibot.objects;

public class GiveawayObject {

    private String guildId;
    private String textchannelId;
    private String messageId;
    private long endTime;
    private String prize;
    private int maxWinners;
    private String emoji;
    private String color;

    public GiveawayObject(String guildId, String textchannelId, String messageId, long endTime, String prize, int maxWinners, String emoji, String color) {
        this.guildId = guildId;
        this.textchannelId = textchannelId;
        this.messageId = messageId;
        this.endTime = endTime;
        this.prize = prize;
        this.maxWinners = maxWinners;
        this.emoji = emoji;
        this.color = color;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getTextchannelId() {
        return textchannelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getPrize() {
        return prize;
    }

    public int getMaxWinners() {
        return maxWinners;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getColor() {
        return color;
    }
}
