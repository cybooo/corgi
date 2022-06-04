package cz.wake.corgibot.objects.user;

import java.util.HashMap;

public class UserWrapper {

    private final String userId;
    private HashMap<String, UserGuildData> guildData;

    public UserWrapper(String userId) {
        this.userId = userId;
        this.guildData = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public HashMap<String, UserGuildData> getGuildData() {
        return guildData;
    }
}
