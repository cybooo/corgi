package cz.wake.corgibot.metrics;

import cz.wake.corgibot.CorgiBot;

public class BotMetrics {

    private long guildCount;
    private long userCount;
    private long textChannelCount;
    private long voiceChannelCount;

    public boolean count() {
        this.guildCount = CorgiBot.getJda().getGuildCache().size();
        this.userCount = CorgiBot.getJda().getUserCache().size();
        this.textChannelCount = CorgiBot.getJda().getTextChannelCache().size();
        this.voiceChannelCount = CorgiBot.getJda().getVoiceChannelCache().size();

        return true;
    }

    public long getGuildCount() {
        return guildCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public long getTextChannelCount() {
        return textChannelCount;
    }

    public long getVoiceChannelCount() {
        return voiceChannelCount;
    }
}
