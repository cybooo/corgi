package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class BotManager {

    private static HashSet<GuildWrapper> listGuilds = new HashSet<>();

    public static void loadGuilds(){
        for(Guild guild : CorgiBot.getJda().getGuilds()){

            //TODO: MOREEEEEEEEE + OPTIMALIZOVAT
            Set<TextChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

            GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(guild.getId());
            gw.setIgnoredChannels(ignoredChannels);

            listGuilds.add(gw);

            System.out.println("Registrace: " + guild.getName());
        }
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
