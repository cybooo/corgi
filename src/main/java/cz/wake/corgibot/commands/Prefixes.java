package cz.wake.corgibot.commands;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;

import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Prefixes {

    private Map<String, String> prefixes = new ConcurrentHashMap<>();

    public Prefixes() {
        try {
            ResultSet set = CorgiBot.getInstance().getSql().getPool().getConnection().createStatement().executeQuery("SELECT * FROM corgibot.prefixes;");
            while (set.next()) {
                prefixes.put(set.getString("guild_id"), set.getString("prefix"));
                CorgiBot.LOGGER.info("Načten prefix: " + set.getString("prefix") + " pro Guild(" + set.getString("guild_id") + ")");
            }
            set.close();
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Nelze načíst prefixy!", e);
        }
    }

    public String get(String guildId) {
        if (guildId == null) {
            return Constants.PREFIX;
        }
        return prefixes.getOrDefault(guildId, Constants.PREFIX);
    }

    public void set(String guildId, String character) {
        if (character == Constants.PREFIX) {
            prefixes.remove(guildId);
            try {
                CorgiBot.getInstance().getSql().deletePrefix(guildId);
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Chyba při mazání prefixu!", e);
            }
            return;
        }
        prefixes.put(guildId, character);
        try {
            CorgiBot.getInstance().getSql().updatePrefix(guildId, String.valueOf(character));
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Chyba při přidávání prefixu!", e);
        }
    }

    public Map<String, String> getPrefixes() {
        return this.prefixes;
    }
}
