package cz.wake.corgibot.objects;

public record GiveawayObject(int giveawayId, String guildId, String textchannelId,
                             String messageId, long endTime, String prize, int maxWinners,
                             String emoji, String color) {

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
