package cz.wake.corgibot.objects;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GuildWrapper {

    /*
        The id of the guild that the settings are for
     */
    private String guildId;

    /*
        Custom prefix for that guild
     */
    private String prefix = Constants.PREFIX;

    /*
        Set of ignored channels where corgi will not react on commands
     */
    private Set<TextChannel> ignoredChannels = new HashSet<>();

    /*
        Sets of blocked commands in guild that corgi wil not react
     */
    private Set<String> blockedCmds = new HashSet<>();

    /*
        Custom tags in guild
     */
    private Map<String, String> tags = new ConcurrentHashMap<>();

    /*
        If is enabled Swear filter for all messages in guild
     */
    private boolean enableSwearFilter = false;

    /*
        If is enabled welcome message for new members in guild
     */
    private boolean enableWelcomeMessage = false;

    /*
        If is guild blocked Corgi will ignore all interaction and commands in guild
     */
    private boolean isBlocked = false;

    /*
        Time for unlock (-1 is permanent)
     */
    private long unBlockTime = -1;

    /*
        Reason for blocking in Corgi
     */
    private String blockReason = null;

    /*
        ID of role which has everything blocked
     */
    private String mutedRoleID = null;

    /*
        Custom guild color for Embeds from Corgi
     */
    private Color customColor = null;

    /*
        If guild has permission to use beta commands
     */
    private boolean isBeta = false;


    /**
     * This will init everything
     *
     * @param guildId the id of the guild that the settings are for
     */
    public GuildWrapper(String guildId) {
        this.guildId = guildId;
    }

    /**
     * This will return the guild id that these options are for
     *
     * @return The id of that guild as a String
     */
    public String getGuildId() {
        return guildId;
    }

    /**
     * Ths will return the prefix that the guild is using
     *
     * @return The prefix that the guild is using
     */
    public String getPrefix() {
        if(prefix == null){
            return Constants.PREFIX;
        }
        return prefix;
    }

    /**
     * List of ignored channels in the guild
     *
     * @return {@link Set} of ignored channels
     */
    public Set<TextChannel> getIgnoredChannels() {
        return ignoredChannels;
    }


    /**
     * List of ignored channels by selected member
     *
     * @param member Selected member in guild
     * @return {@link List}
     */
    public List<TextChannel> getIgnoredChannelsByMember(Member member) {
        ArrayList<TextChannel> ignoredChannels = new ArrayList<>();
        for (TextChannel tc : this.ignoredChannels) {
            if (member.getGuild().getTextChannels().contains(tc)) {
                ignoredChannels.add(tc);
            }
        }
        return ignoredChannels;
    }

    /**
     * List of blocked commands in the guild
     *
     * @return {@link Set} of blocked commands
     */
    public Set<String> getBlockedCmds() {
        return blockedCmds;
    }

    /**
     * Map of Tags for the guild
     * @return {@link Map} of tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * If is Swear Filter enabled in the guild
     *
     * @return true if is enabled
     */
    public boolean isEnabledSwearFilter() {
        return enableSwearFilter;
    }

    /**
     * If is welcome message enabled in the guild
     *
     * @return true if is enabled
     */
    public boolean isEnabledWelcomeMessage() {
        return enableWelcomeMessage;
    }

    /**
     * If is Corgi completely disabled in the guild
     *
     * @return true if is blocked
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Return time in {@link Long} value when will
     * be Corgi unblocked for the guild.
     *
     * @return Time in miliseconds
     */
    public long getUnBlockTime() {
        return unBlockTime;
    }

    /**
     * Returns reason for of blocking in the guild
     *
     * @return String of Message
     */
    public String getBlockReason() {
        return blockReason;
    }

    /**
     * Returns Id of role that is proposed to use like muted
     *
     * @return Id of role
     */
    public String getMutedRoleID() {
        return mutedRoleID;
    }

    /**
     * Returns {@link Color} that will be used for Embeds
     * in Corgi in the guild.
     *
     * @return Custom color
     */
    public Color getCustomColor() {
        return customColor;
    }

    /**
     * If is true the guild have access to beta
     * commands.
     *
     * @return true if have access
     */
    public boolean isBeta() {
        return isBeta;
    }

    /**
     * Update prefix for the guild!
     * 
     * @param prefix Custom prefix
     * @param updateSQL boolean value if we want to update prefix in SQL
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setPrefix(String prefix, boolean updateSQL) {
        if(updateSQL){
            if(prefix == Constants.PREFIX){
                this.prefix = Constants.PREFIX;
                try {
                    CorgiBot.getInstance().getSql().updatePrefix(guildId, prefix);
                } catch (Exception e) {
                    CorgiBot.LOGGER.error("Chyba při mazání prefixu!", e);
                }
                return this;
            }
            this.prefix = prefix;
            try {
                CorgiBot.getInstance().getSql().updatePrefix(guildId, prefix);
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Chyba při přidávání prefixu!", e);
            }
        }
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets ignored channels for this guild
     *
     * @param ignoredChannel Ignored channels
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setIgnoredChannels(Set<TextChannel> ignoredChannel) {
        this.ignoredChannels = ignoredChannel;
        return this;
    }

    /**
     * Update ignored channels in the guild
     * If textchannels exists will be deleted from list, if not will be added
     *
     * @param textChannel Selected channel
     * @return {@link GuildWrapper}
     */
    public GuildWrapper updateIgnoredChannel(TextChannel textChannel) {
        if (ignoredChannels.contains(textChannel)) {
            ignoredChannels.remove(textChannel);
            try {
                CorgiBot.getInstance().getSql().deleteIgnoredChannel(textChannel.getId());
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Chyba při mazání ignorovaného channelu!", e);
            }
            return this;
        }
        this.ignoredChannels.add(textChannel);
        try {
            CorgiBot.getInstance().getSql().addIgnoredChannel(this.guildId, textChannel.getId());
        } catch (Exception e) {
            CorgiBot.LOGGER.error("Chyba při přidávání ignorovaného channelu!", e);
        }
        return this;
    }

    /**
     * Sets blocked commands for this guild
     *
     * @param blockedCmds Blocked cmds
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setBlockedCmds(Set<String> blockedCmds) {
        this.blockedCmds = blockedCmds;
        return this;
    }

    /**
     * Sets map of custom tags in the guild
     *
     * @param tags Map of tags
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Sets state of Swear filter in the guild.
     * Swear filter will delete swears in all channels.
     *
     * @param enableSwearFilter Boolean value
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setEnableSwearFilter(boolean enableSwearFilter) {
        this.enableSwearFilter = enableSwearFilter;
        return this;
    }

    /**
     * Sets custom welcome message for the guild.
     *
     * @param enableWelcomeMessage String of texts
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setEnableWelcomeMessage(boolean enableWelcomeMessage) {
        this.enableWelcomeMessage = enableWelcomeMessage;
        return this;
    }

    /**
     * Sets the guild to blocked state
     *
     * @param blocked Boolean value
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setBlocked(boolean blocked) {
        isBlocked = blocked;
        return this;
    }

    /**
     * Time for unblocking this guild
     *
     * @param unBlockTime Long value of time
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setUnBlockTime(long unBlockTime) {
        this.unBlockTime = unBlockTime;
        return this;
    }

    /**
     * Revoke blocking
     */
    public GuildWrapper revokeBlock() {
        this.unBlockTime = -1; // -1 infinite and revoked
        this.blockReason = "";
        this.isBlocked = false;
        return this;
    }

    /**
     * Sets reason, why is this guild blocked in Corgi.
     *
     * @param blockReason Reason for blocking
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setBlockReason(String blockReason) {
        this.blockReason = blockReason;
        return this;
    }

    /**
     * Sets Id for muted role
     * Muted role is role that can not write into all channels.
     *
     * @param mutedRoleID Id of role
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setMutedRoleID(String mutedRoleID) {
        this.mutedRoleID = mutedRoleID;
        return this;
    }

    /**
     * Sets custom color for all embeds in the guild
     *
     * @param customColor {@link Color} of embeds
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setCustomColor(Color customColor) {
        this.customColor = customColor;
        return this;
    }

    /**
     * If is the guild has access to beta commands
     *
     * @param beta Boolean of this fact :D
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setBeta(boolean beta) {
        isBeta = beta;
        return this;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "[id=" + guildId + ",prefix=" + prefix + ",ignoredChannels=" + ignoredChannels.toString() + "]";
    }
}
