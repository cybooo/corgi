package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.objects.user.UserWrapper;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.*;

public class BotManager {

    private static final HashSet<GuildWrapper> GUILD_WRAPPERS = new HashSet<>();
    private static final HashMap<String, UserWrapper> USER_WRAPPERS = new HashMap<>();
    public static final List<String> DISABLED_SLASH_NOTICES = new ArrayList<>();

    public static void registerOrLoadGuild(Guild guild) {

        if (guild == null) {
            CorgiLogger.warnMessage("Guild is null!");
            return;
        }

        if (CorgiBot.getInstance().getSql().existsGuildData(guild.getId())) {
            if (getCustomGuild(guild.getId()) == null) {
                // Load dat from SQL + load into BotManager
                Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrapper(guild.getId());
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

    public static void loadGuilds(int shardId) {
        List<Guild> guildList = CorgiBot.getShardManager().getShardById(shardId).getGuilds();
        for (Guild guild : guildList) {
            if (getCustomGuild(guild.getId()) != null) {
                CorgiLogger.infoMessage("Guild " + guild.getId() + " is already loaded!");
                continue;
            }
            try {
                // Setup ignored channels
                Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(guild.getId());

                // Setup guild wrapper with ignored channels
                GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrapper(guild.getId());

                if (gw != null) {
                    gw.setIgnoredChannels(ignoredChannels);
                    gw.setLanguage(CorgiBot.getInstance().getSql().getLanguage(guild.getId()), false);
                    GUILD_WRAPPERS.add(gw);
                }
                CorgiBot.getInstance().getSql().getGiveawaysByGuild(guild.getId()).forEach(giveaway -> {
                    try {
                        new Giveaway2(getCustomGuild(giveaway.getGuildId()), CorgiBot.getShardManager().getGuildById(giveaway.getGuildId()).getTextChannelById(giveaway.getTextchannelId()).retrieveMessageById(giveaway.getMessageId()).complete(true), giveaway.getEndTime(), giveaway.getPrize(), giveaway.getMaxWinners(), giveaway.getEmoji(), giveaway.getColor()).start();
                    } catch (Exception e) {
                        exceptionHandler(e, giveaway.getGuildId(), giveaway.getMessageId());
                    }
                });
            } catch (Exception ex) {
                CorgiLogger.dangerMessage("Error while registering guild (ID: " + guild.getId() + "). Error:\n");
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static boolean loadUser(String userId) {
        try {
            UserWrapper userWrapper = CorgiBot.getInstance().getSql().createUserWrapper(userId);
            if (userWrapper != null) {
                for (String guildId : userWrapper.getGuildData().keySet()) {
                    userWrapper.getGuildData().get(guildId).setLevel(CorgiBot.getInstance().getSql().getLevel(userId, guildId), false);
                    userWrapper.getGuildData().get(guildId).setMessages(CorgiBot.getInstance().getSql().getMessages(userId, guildId), false);
                    userWrapper.getGuildData().get(guildId).setXp(CorgiBot.getInstance().getSql().getXp(userId, guildId), false);
                    userWrapper.getGuildData().get(guildId).setVoiceTime(CorgiBot.getInstance().getSql().getVoiceTime(userId, guildId), false);
                }
                USER_WRAPPERS.put(userId, userWrapper);
                return true;
            }
        } catch (Exception ex) {
            CorgiLogger.dangerMessage("Error while loading user (ID: " + userId + "). Error:\n");
            ex.printStackTrace();
            System.exit(-1);
        }
        return false;
    }

    public static void addGuild(GuildWrapper gw) {
        GUILD_WRAPPERS.add(gw);
    }

    public static void removeGuild(GuildWrapper gw) {
        GUILD_WRAPPERS.remove(gw);
    }

    public static void addUser(UserWrapper uw) {
        USER_WRAPPERS.put(uw.getUserId(), uw);
    }

    public static void removeUser(UserWrapper uw) {
        USER_WRAPPERS.remove(uw.getUserId());
    }

    public static GuildWrapper getCustomGuild(String id) {
        for (GuildWrapper w : GUILD_WRAPPERS) {
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

    public static HashSet<GuildWrapper> getGuildWrappers() {
        return GUILD_WRAPPERS;
    }

    public static HashMap<String, UserWrapper> getUserWrappers() {
        return USER_WRAPPERS;
    }

}
