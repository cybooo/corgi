package cz.wake.corgibot.objects.user;

import cz.wake.corgibot.CorgiBot;

public class UserGuildData {

    private String userId;
    private String guildId;
    private long level;
    private long xp;
    private long voiceTime;
    private long messages;

    public UserGuildData(String userId, String guildId) {
        this.userId = userId;
        this.guildId = guildId;
        this.level = 1;
        this.xp = 0;
        this.voiceTime = 0;
        this.messages = 0;
    }

    public long getLevel() {
        return level;
    }

    public UserGuildData setLevel(long level) {
        CorgiBot.getInstance().getSql().setLevel(userId, guildId, level);
        this.level = level;
        return this;
    }

    public UserGuildData addLevel(long level) {
        CorgiBot.getInstance().getSql().setLevel(userId, guildId, getLevel() + level);
        this.level += level;
        return this;
    }

    public long getXp() {
        return xp;
    }

    public UserGuildData setXp(long xp) {
        CorgiBot.getInstance().getSql().setXp(userId, guildId, xp);
        this.xp = xp;
        return this;
    }

    public UserGuildData addXp(long xp) {
        CorgiBot.getInstance().getSql().setXp(userId, guildId, getXp() + xp);
        this.xp += xp;
        return this;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public UserGuildData setVoiceTime(long voiceTime) {
        CorgiBot.getInstance().getSql().setVoiceTime(userId, guildId, voiceTime);
        this.voiceTime = voiceTime;
        return this;
    }

    public UserGuildData addVoiceTime(long voiceTime) {
        CorgiBot.getInstance().getSql().setVoiceTime(userId, guildId, getVoiceTime() + voiceTime);
        this.voiceTime += voiceTime;
        return this;

    }

    public long getMessages() {
        return messages;
    }

    public UserGuildData setMessages(long messages) {
        CorgiBot.getInstance().getSql().setMessages(userId, guildId, messages);
        this.messages = messages;
        return this;
    }

    public UserGuildData addMessages(long messages) {
        CorgiBot.getInstance().getSql().setMessages(userId, guildId, getMessages() + messages);
        this.messages += messages;
        return this;
    }

    @Override
    public String toString() {
        return "UserGuildData{" +
                "userId='" + userId + '\'' +
                ", guildId='" + guildId + '\'' +
                ", level=" + level +
                ", xp=" + xp +
                ", voiceTime=" + voiceTime +
                ", messages=" + messages +
                '}';
    }
}
