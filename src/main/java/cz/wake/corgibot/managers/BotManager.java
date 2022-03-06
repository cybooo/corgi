package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.HashSet;
import java.util.Set;

public class BotManager {

    private static final HashSet<GuildWrapper> listGuilds = new HashSet<>();

    public static GuildWrapper getGuildWrapper(Guild guild) {
        GuildWrapper guildWrapper;
        if (!CorgiBot.isIsBeta()) {
            guildWrapper = BotManager.getCustomGuild(guild.getId());
        } else {
            guildWrapper = new GuildWrapper(guild.getId());
        }
        return guildWrapper;
    }

    public static GuildWrapper getGuildWrapper(String guildid) {
        GuildWrapper guildWrapper;
        if (!CorgiBot.isIsBeta()) {
            guildWrapper = BotManager.getCustomGuild(guildid);
        } else {
            guildWrapper = new GuildWrapper(guildid);
        }
        return guildWrapper;
    }

    public static void registerOrLoadGuild(Guild guild) {
        if (CorgiBot.getInstance().getSql().existsGuildData(guild.getId())) {
            if (getCustomGuild(guild.getId()) == null) {
                // Load dat from SQL + load into BotManager
                Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(guild.getId());
                gw.setIgnoredChannels(ignoredChannels);
                gw.setPrefix("c!", true); // Reset prefix to c!
                BotManager.addGuild(gw);
            }
        } else {
            // INSERT DAT + insert into BotManager
            CorgiBot.getInstance().getSql().insertDefaultServerData(guild.getId(), "c!");
            GuildWrapper gw = new GuildWrapper(guild.getId());
            gw.setPrefix("c!", false);
            BotManager.addGuild(gw);
        }
    }

    public static void loadGuilds() {
        CorgiBot.getJda().getGuilds().forEach(guild -> {
            try {
                // Setup ignored channels
                Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

                // Setup guild wrapper with ignored channels
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(guild.getId());
                if (gw != null) {
                    gw.setIgnoredChannels(ignoredChannels);
                }

                if (gw != null) {
                    gw.setLanguage("cz", false); //TODO: SQL
                    listGuilds.add(gw);
                }

            } catch (Exception ex) {
                CorgiLogger.dangerMessage("Error when registering guild (ID: " + guild.getId() + "). Error:\n");
                ex.printStackTrace();
                System.exit(-1);
            }
        });
        CorgiLogger.greatMessage("Connected on (" + listGuilds.size() + ") servers!");
        CorgiLogger.infoMessage("Loading Giveaways on guilds.");
        CorgiBot.getInstance().getSql().getAllGiveaways().forEach(go -> {
            try {
                new Giveaway2(CorgiBot.getJda().getGuildById(go.getGuildId()).getTextChannelById(go.getTextchannelId()).retrieveMessageById(go.getMessageId()).complete(true), go.getEndTime(), go.getPrize(), go.getMaxWinners(), go.getEmoji(), go.getColor()).start();
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
        if (ex instanceof ErrorResponseException e) {
            switch (e.getErrorCode()) {

                // Giveaway deleted.. Corgi does not have access to message
                case 10008, 10003 -> { // 10008 = message not found | 10003 = channel not found
                    CorgiLogger.fatalMessage("Detection deleted Giveaway (G:" + guildId + " | M:" + messageId + "). Giveaway stopped!");
                    CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(guildId, messageId);
                }

                // Missing permissions for editing message
                case 50001, 50013 -> { // 50001 = missing access | 50013 = missing permissions
                    CorgiLogger.fatalMessage("Detection wrong permissions (G:" + guildId + " | M:" + messageId + "). Giveaway stopped!");
                    CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(guildId, messageId);
                }
                default -> {
                    CorgiLogger.fatalMessage("Something went wrong! (G:" + guildId + " | M:" + messageId + ")");
                }
            }
        }
    }
}
