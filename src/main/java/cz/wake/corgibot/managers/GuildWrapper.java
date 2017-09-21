package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashSet;

public class GuildWrapper {

    private String guildId;
    private String guildPrefix;
    private HashSet<String> ignoredChannles = new HashSet<String>();
    private boolean blocked = false;

    public GuildWrapper(String guildID) {
        this.guildId = guildID;
    }

    public Guild getGuild(){
        return CorgiBot.getJda().getGuildById(guildId);
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getGuildPrefix() {
        return guildPrefix;
    }

    public HashSet<String> getIgnoredChannles() {
        return ignoredChannles;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setGuildPrefix(String guildPrefix) {
        this.guildPrefix = guildPrefix;
    }

    public void setIgnoredChannles(HashSet<String> ignoredChannles) {
        this.ignoredChannles = ignoredChannles;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
