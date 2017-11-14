package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IgnoredChannels {

    private Map<Guild, TextChannel> channels = new ConcurrentHashMap<>();

    public IgnoredChannels() {
        try {
            ResultSet set = CorgiBot.getInstance().getSql().getPool().getConnection().createStatement().executeQuery("SELECT * FROM corgibot.ignored_channels;");
            while (set.next()) {
                channels.put(CorgiBot.getJda().getGuildById(set.getString("guild_id")), CorgiBot.getJda().getTextChannelById(set.getString("channel_id")));
                System.out.println("Přidán: " + set.getString("guild_id") + " - " + set.getString("channel_id"));
            }
            set.close();
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Nelze načíst ignorované channely!", e);
        }
    }

    public boolean isBlocked(MessageChannel channelId) {
        return channels.containsValue(channelId);
    }

    public void set(Guild guildId, TextChannel channelId) {
        if (channels.containsValue(channelId)) {
            channels.remove(guildId);
            try {
                CorgiBot.getInstance().getSql().deleteIgnoredChannel(channelId.getId());
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Chyba při mazání prefixu!", e);
            }
            return;
        }
        channels.put(guildId, channelId);
        try {
            CorgiBot.getInstance().getSql().addIgnoredChannel(guildId.getId(), channelId.getId());
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Chyba při přidávání prefixu!", e);
        }
    }

    public Map<Guild, TextChannel> getIgnoredChannels() {
        return this.channels;
    }

    public List<MessageChannel> getIgnoredGuildChannels(Member member){
        ArrayList<MessageChannel> ignoredChannels = new ArrayList<>();
        for(int i = 0; i < this.channels.size(); i++){
            if(this.channels.containsKey(member.getGuild())){
                ignoredChannels.add(member.getGuild().getTextChannelById(this.channels.toString().indexOf(i)));
            }
        }
        System.out.println(ignoredChannels.toString());
        return ignoredChannels;
    }
}
