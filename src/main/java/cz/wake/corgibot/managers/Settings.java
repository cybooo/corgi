package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.core.entities.Guild;

public class Settings {

    private String guildID;
    private String prefix;
    private String autoRoleID;
    private boolean antiAd = false;
    private String locale = "en_US";

    public Settings(String guildID) {
        this.guildID = guildID;
    }

    public Settings(Guild guild) {
        this.guildID = guild.getId();
    }

    public Settings setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Settings setAutoRoleID(String autoRoleID) {
        this.autoRoleID = autoRoleID;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getAutoRoleID() {
        return autoRoleID;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    /**
     * Saves all the settings into mysql, allowing for later reading/writing.
     */
    public void defaultSave() {
        CorgiLogger.warnMessage("Guild " + CorgiBot.getJda().getGuildById(this.guildID).getName() + " does not exists!");
        CorgiBot.getInstance().getSql().insertDefaultServerData(this.guildID);
        CorgiLogger.greatMessage("Guild has been successfully registred.");
    }

    /**
     * Gets a settings object based on a guild.
     *
     * @param guild The guild we're getting settings for.
     * @return a {@link Settings} object containing data related to the guild.
     * @see Settings#getSettings(String)
     */
    public static Settings getSettings(Guild guild) {
        return getSettings(guild.getId());
    }

    /**
     * Gets a settings object based on a guild's ID.
     *
     * @param guildID The guild's ID we're using to get get settings for.
     * @return a {@link Settings} object containing data related to the guild.
     */
    public static Settings getSettings(String guildID) {
        return null;
    }

    /**
     * Gets a settings object based on a guild, may return null if no settings exist.
     *
     * @param guildID The guild's ID we're using to get get settings for.
     * @return a {@link Settings} object containing data related to the guild, or null if no settings exist.
     */
    public static Settings getSettingsOrNull(String guildID) {
        return null;
    }

    public Settings setAntiAd(boolean antiAd) {
        this.antiAd = antiAd;
        return this;
    }

    public boolean isAntiAdEnabled() {
        return antiAd;
    }


}
