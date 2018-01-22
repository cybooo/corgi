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

            String oldPrefix;

            if(!CorgiBot.getInstance().getSql().existsGuildData(guild.getId())){

                System.out.println("DEBUG Spouštím import serveru " + guild.getName());

                // Import prefixu z stary databaze...
                oldPrefix = CorgiBot.getInstance().getSql().getPrefixOverSQL(guild.getId());
                if(oldPrefix == null){
                    oldPrefix = Constants.PREFIX;
                }

                // Import serveru do databaze
                CorgiBot.getInstance().getSql().insertDefaultServerData(guild.getId(), oldPrefix);
                CorgiBot.getInstance().getSql().deleteOldData(guild.getId()); // Smazani starych zaznamu

            }

            //TODO: MOREEEEEEEEE + OPTIMALIZOVAT
            Set<TextChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

            // Object
            GuildWrapper gw = new GuildWrapper(guild.getId());

            try {
                ResultSet set = CorgiBot.getInstance().getSql().getPool().getConnection().createStatement().executeQuery("SELECT * FROM corgibot.guild_data WHERE guild_id = " + guild.getId() + ";");
                while (set.next()) {
                    try {
                        gw.setIgnoredChannels(ignoredChannels).setPrefix(set.getString("prefix"));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                set.close();
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Nelze načíst ignorované channely!");
                return; // Must stop...
            }

            listGuilds.add(gw);

            System.out.println("[DEBUG]: Objekt: " + gw.toString());
            System.out.println("[DEBUG]: BotManager list: " + listGuilds);

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
