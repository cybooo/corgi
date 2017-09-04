package cz.wake.corgibot.managers;

import java.util.HashSet;

public class Guilder {

    private String guildID;
    private String guildOwnerID;
    private String guildPrefix;
    private HashSet<String> ignoredChannles;

    public Guilder(String guildID, String guildOwnerID, String guildPrefix) {
        this.guildID = guildID;
        this.guildOwnerID = guildOwnerID;
        this.guildPrefix = guildPrefix;
        this.ignoredChannles = new HashSet<>();
    }

    public String getGuildID() {
        return guildID;
    }

    public String getGuildOwnerID() {
        return guildOwnerID;
    }

    public String getGuildPrefix() {
        return guildPrefix;
    }

    public HashSet<String> getIgnoredChannles() {
        return ignoredChannles;
    }
}
