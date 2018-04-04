package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BotManager {

    private static HashSet<GuildWrapper> listGuilds = new HashSet<>();

    public static void loadGuilds(){
        for(Guild guild : CorgiBot.getJda().getGuilds()){
            try {
                // Setup ignored channels
                Set<TextChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

                // Setup guild wrapper with ignored channels
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(guild.getId());
                gw.setIgnoredChannels(ignoredChannels);

                // Register in bot
                listGuilds.add(gw);
            } catch (NullPointerException ex){
                CorgiLogger.dangerMessage("Nastala chyba pri registraci serveru! Zprava:");
                ex.printStackTrace();
                System.exit(-1);
            }
        }
        CorgiLogger.greatMessage("Pripojeno na (" + listGuilds.size() + ") serveru!");
    }

    public static HashSet<GuildWrapper> getListGuilds() {
        return listGuilds;
    }

    public static void addGuild(GuildWrapper gw){
        listGuilds.add(gw);
    }

    public static void removeGuild(GuildWrapper gw){
        listGuilds.remove(gw);
    }

    public static GuildWrapper getCustomGuild(String id){
        for(GuildWrapper w : listGuilds){
            if(w.getGuildId().equals(id)){
                return w;
            }
        }
        return null; // Neni v cachce?
    }
}
