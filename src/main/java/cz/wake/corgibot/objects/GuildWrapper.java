package cz.wake.corgibot.objects;

import cz.wake.corgibot.utils.Constants;

import java.awt.*;
import java.util.*;
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
    private Set<String> ignoredChannels = new HashSet<>();

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
        return prefix;
    }

    /**
     * List of ignored channels in the guild
     *
     * @return {@link Set} of ignored channels
     */
    public Set<String> getIgnoredChannels() {
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
     * Sets custom prefix for this guild
     * 
     * @param prefix Custom prefix
     * @return {@link GuildWrapper}
     */
    public GuildWrapper setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GuildWrapper setIgnoredChannels(Set<String> ignoredChannels) {
        this.ignoredChannels = ignoredChannels;
    }

    public GuildWrapper setBlockedCmds(Set<String> blockedCmds) {
        this.blockedCmds = blockedCmds;
    }

    public GuildWrapper setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public GuildWrapper setEnableSwearFilter(boolean enableSwearFilter) {
        this.enableSwearFilter = enableSwearFilter;
    }

    public GuildWrapper setEnableWelcomeMessage(boolean enableWelcomeMessage) {
        this.enableWelcomeMessage = enableWelcomeMessage;
    }

    public GuildWrapper setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public GuildWrapper setUnBlockTime(long unBlockTime) {
        this.unBlockTime = unBlockTime;
    }

    public GuildWrapper setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public GuildWrapper setMutedRoleID(String mutedRoleID) {
        this.mutedRoleID = mutedRoleID;
    }

    public GuildWrapper setCustomColor(Color customColor) {
        this.customColor = customColor;
    }

    public GuildWrapper setBeta(boolean beta) {
        isBeta = beta;
    }
}
