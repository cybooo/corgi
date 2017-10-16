package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Prefixes {

    private Map<String, Character> prefixes = new ConcurrentHashMap<>();

    public Prefixes() {
        try {
            ResultSet set = CorgiBot.getInstance().getSql().getPool().getConnection().createStatement().executeQuery("SELECT * FROM corgibot.prefixes;");
            while(set.next()){
                prefixes.put(set.getString("guild_id"), set.getString("prefix").charAt(0));
                CorgiBot.LOGGER.info("Načten prefix: " + set.getString("prefix") + " pro Guild(" + set.getString("guild_id") + ")");
            }
            set.close();
        } catch (Exception e){
            CorgiBot.LOGGER.error("Nelze načíst prefixy!", e);
        }
    }

    public char get(String guildId) {
        if (guildId == null){
            return CorgiBot.PREFIX;
        }
        return prefixes.getOrDefault(guildId, CorgiBot.PREFIX);
    }

    public void set(String guildId, char character) {
        if (character == CorgiBot.PREFIX) {
            prefixes.remove(guildId);
            try {
                CorgiBot.getInstance().getSql().deletePrefix(guildId);
            } catch (Exception e){
                CorgiBot.LOGGER.error("Chyba při mazání prefixu!", e);
            }
            return;
        }
        prefixes.put(guildId, character);
        try {
            CorgiBot.getInstance().getSql().updatePrefix(guildId, String.valueOf(character));
        } catch (Exception e){
            CorgiBot.LOGGER.error("Chyba při přidávání prefixu!", e);
        }
    }

    public Map<String, Character> getPrefixes() {
        return this.prefixes;
    }
}
