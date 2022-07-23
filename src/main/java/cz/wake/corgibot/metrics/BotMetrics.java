package cz.wake.corgibot.metrics;

import cz.wake.corgibot.CorgiBot;

public class BotMetrics {

    private long guildCount;
    private long userCount;
    private long textChannelCount;
    private long voiceChannelCount;

    public boolean count() {
        this.guildCount = CorgiBot.getShardManager().getGuildCache().size();
        this.userCount = CorgiBot.getShardManager().getUserCache().size();
        this.textChannelCount = CorgiBot.getShardManager().getTextChannelCache().size();
        this.voiceChannelCount = CorgiBot.getShardManager().getVoiceChannelCache().size();
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
