package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.util.HashSet;
import java.util.Set;

public class BotManager {

    private static HashSet<GuildWrapper> listGuilds = new HashSet<>();

    public static void loadGuilds() {
        CorgiBot.getJda().getGuilds().forEach(guild -> {
            try {
                // Setup ignored channels
                Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

                // Setup guild wrapper with ignored channels
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(guild.getId());
                if(ignoredChannels != null && gw != null){
                    gw.setIgnoredChannels(ignoredChannels);
                }

                if (gw != null) {
                    gw.setLanguage("cz", false); //TODO: SQL
                    listGuilds.add(gw);
                }

            } catch (NullPointerException ex) {
                CorgiLogger.dangerMessage("Error when Corgi register Guild (ID: " + guild.getId() + "). Error:\n");
                ex.printStackTrace();
                System.exit(-1);
            }
        });
        CorgiLogger.greatMessage("Connected on (" + listGuilds.size() + ") serveru!");
        CorgiLogger.infoMessage("Loading Giveaways on guilds.");
        CorgiBot.getInstance().getSql().getAllGiveaways().forEach(go -> {
            try {
                new Giveaway2(CorgiBot.getJda().getGuildById(go.getGuildId()).getTextChannelById(go.getTextchannelId()).getMessageById(go.getMessageId()).complete(true), go.getEndTime(), go.getPrize(), go.getMaxWinners(), go.getEmoji(), go.getColor()).start();
            } catch (Exception e) {
                exceptionHandler(e, go.getGuildId(), go.getMessageId());
            }
        });
    }

    public static HashSet<GuildWrapper> getListGuilds() {
        return listGuilds;
    }

    public static void addGuild(GuildWrapper gw) {
        listGuilds.add(gw);
    }

    public static void removeGuild(GuildWrapper gw) {
        listGuilds.remove(gw);
    }

    public static GuildWrapper getCustomGuild(String id) {
        for (GuildWrapper w : listGuilds) {
            if (w.getGuildId().equals(id)) {
                return w;
            }
        }
        return null; // Is not in cache?
    }

    private static void exceptionHandler(Throwable ex, String guildId, String messageId) {
        if (ex instanceof ErrorResponseException) {
            ErrorResponseException e = (ErrorResponseException) ex;
            switch (e.getErrorCode()) {

                // Giveaway deleted.. Corgi do not have access to message
                case 10008: // message not found
                case 10003: // channel not found
                    CorgiLogger.fatalMessage("Detection deleted Giveaway (G:" + guildId + " | M:" + messageId + "). Giveaway stopped!");
                    CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(guildId, messageId);
                    break;

                // Missing permissions for editing message
                case 50001: // missing access
                case 50013: // missing permissions
                    CorgiLogger.fatalMessage("Detection wrong permissions (G:" + guildId + " | M:" + messageId + "). Giveaway stopped!");
                    CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(guildId, messageId);
                    break;

            }
        }
    }
}
