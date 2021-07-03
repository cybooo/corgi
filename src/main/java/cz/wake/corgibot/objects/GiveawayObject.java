package cz.wake.corgibot.objects;

public class GiveawayObject {

    private final int giveawayId;
    private final String guildId;
    private final String textchannelId;
    private final String messageId;
    private final long endTime;
    private final String prize;
    private final int maxWinners;
    private final String emoji;
    private final String color;

    public GiveawayObject(int giveawayId, String guildId, String textchannelId, String messageId, long endTime, String prize, int maxWinners, String emoji, String color) {
        this.giveawayId = giveawayId;
        this.guildId = guildId;
        this.textchannelId = textchannelId;
        this.messageId = messageId;
        this.endTime = endTime;
        this.prize = prize;
        this.maxWinners = maxWinners;
        this.emoji = emoji;
        this.color = color;
    }

    public int getGiveawayId() {
        return giveawayId;
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
