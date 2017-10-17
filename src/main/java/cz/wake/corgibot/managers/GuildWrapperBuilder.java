package cz.wake.corgibot.managers;

import java.util.HashSet;

public class GuildWrapperBuilder {

    private GuildWrapper wrapper;

    public GuildWrapperBuilder(String guildId) {
        this.wrapper = new GuildWrapper(guildId);
    }

    public GuildWrapperBuilder setBlocked(boolean blocked){
        this.wrapper.setBlocked(blocked);
        return this;
    }

    public GuildWrapperBuilder setIgnoredChannels(HashSet<String> channels){
        this.wrapper.setIgnoredChannles(channels);
        return this;
    }

    public GuildWrapper build() {
        return wrapper;
    }
}
