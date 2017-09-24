package cz.wake.corgibot;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Prefixes {

    private Map<String, Character> prefixes = new ConcurrentHashMap<>();

    public Prefixes() {
        try {
            ResultSet set = CorgiBot.getInstance().getSql().getPrefixData();
            while(set.next()){
                prefixes.put(set.getString("guildid"), set.getString("prefix").charAt(0));
            }
        } catch (Exception e){
            CorgiBot.LOGGER.error("Nelze načíst prefixy!", e);
        }

    }
}
