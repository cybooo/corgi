package cz.wake.corgibot.commands;

import cz.wake.corgibot.managers.Settings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommandClient extends ListenerAdapter {

    private final OffsetDateTime start;

    private final String botOwner;
    private final List<String> botAdmins;
    private final List<String> botMods;

    private String prefix = "c!";
    private final HashMap<String, String> customPrefixes;

    private String defaultLocale = "en_US";
    private final HashMap<String, String> customLocales;

    private final ArrayList<Command> commands;
    private final HashMap<String, Integer> commandIndex;
    private final HashMap<String, OffsetDateTime> cooldowns;

    //Key = User ID, Value = Disable Type. (0 = Tickets only, 1 = Commands only, 2 = Blocked entirely.)
    private final HashMap<String, Integer> disabledUsers;

    //Key = Guild ID, Value = List of User IDs.
    private final HashMap<String, List<String>> disabledMembers;

    //Key = Channel ID, Value = List of User IDs.
    private final HashMap<String, List<String>> activeQuestionnaires;

    public CommandClient() {
        start = OffsetDateTime.now();

        customPrefixes = new HashMap<>();
        commands = new ArrayList<>();
        cooldowns = new HashMap<>();
        commandIndex = new HashMap<>();
        customLocales = new HashMap<>();
        disabledUsers = new HashMap<>();
        disabledMembers = new HashMap<>();
        activeQuestionnaires = new HashMap<>();

        botOwner = "kek";
        botAdmins = new ArrayList<>(); //TODO: Config
        botMods = new ArrayList<>();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setCustomPrefix(String guildID, String prefix) {
        if (customPrefixes.containsKey(guildID)) {
            if (prefix.equals(this.prefix)) customPrefixes.remove(guildID);
            else customPrefixes.replace(guildID, prefix);
        } else {
            if (prefix.equals(this.prefix)) return;
            customPrefixes.put(guildID, prefix);
        }
    }

    public void setCustomLocale(String guildID, String locale) { //TODO: Locale update (next)

        if (customLocales.containsKey(guildID)) {
            if (locale.equals(customLocales.get(guildID))) return;

            if (locale.equals(this.defaultLocale)) customLocales.remove(guildID);
            else customLocales.replace(guildID, locale);
        } else {
            if (locale.equals(this.defaultLocale)) return;
            customLocales.put(guildID, locale);
        }
    }

    public void addCommand(Command command) {
        String name = command.getName();
        synchronized (commandIndex) {
            if (commandIndex.containsKey(name))
                throw new IllegalArgumentException("Command added already has a name/alias that was already indexed: \"" + name + "\"!");
            for (String alias : command.getAliases()) {
                if (commandIndex.containsKey(alias))
                    throw new IllegalArgumentException("Command added already has a name/alias that was already indexed: \"" + name + "\"!");
                commandIndex.put(alias, commands.size());
            }
            commandIndex.put(name, commands.size());
        }

        commands.add(command);
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void disableUser(String ID, int type) {
        if (!disabledUsers.containsKey(ID)) disabledUsers.put(ID, type);
    }

    public void undisableUser(String ID) {
        if (disabledUsers.containsKey(ID)) disabledUsers.remove(ID);
    }

    public void disableMember(String guildID, String userID) {
        if (!disabledMembers.containsKey(guildID)) disabledMembers.put(guildID, new ArrayList<>());
        disabledMembers.get(guildID).add(userID);
    }

    public void registerQuestionnaire(String channelID, String userID) {
        if (!activeQuestionnaires.containsKey(channelID)) activeQuestionnaires.put(channelID, new ArrayList<>());
        activeQuestionnaires.get(channelID).add(userID);
    }

    public void unregisterQuestionnaire(String channelID, String userID) {
        if (!activeQuestionnaires.containsKey(channelID)) return;
        if (activeQuestionnaires.get(channelID).contains(userID)) activeQuestionnaires.get(channelID).remove(userID);
    }

    public void undisableMember(String guildID, String userID) {
        if (!disabledMembers.containsKey(guildID)) return;
        if (disabledMembers.get(guildID).contains(userID)) disabledMembers.get(guildID).remove(userID);
    }


    public OffsetDateTime getCooldown(String name) {
        return cooldowns.get(name);
    }

    public int getRemainingCooldown(String name) {
        if (cooldowns.containsKey(name)) {
            int time = (int) OffsetDateTime.now().until(cooldowns.get(name), ChronoUnit.SECONDS);
            if (time <= 0) {
                cooldowns.remove(name);
                return 0;
            }
            return time;
        }
        return 0;
    }

    public void applyCooldown(String name, int seconds) {
        cooldowns.put(name, OffsetDateTime.now().plusSeconds(seconds));
    }

    public void cleanCooldowns() {
        OffsetDateTime now = OffsetDateTime.now();
        cooldowns.keySet().stream().filter((str) -> (cooldowns.get(str).isBefore(now)))
                .collect(Collectors.toList()).stream().forEach(str -> cooldowns.remove(str));
    }

    public String getPrefix(String guildID) {
        return customPrefixes.getOrDefault(guildID, prefix);
    }

    public String getLocale(String guildID) {
        return customLocales.getOrDefault(guildID, defaultLocale);
    }

    public boolean isUserDisabled(String userID) {
        return disabledUsers.containsKey(userID) && disabledUsers.get(userID) >= 1;
    }

    public boolean canUseTickets(String userID) {
        return !(disabledUsers.containsKey(userID) && disabledUsers.get(userID) >= 0);
    }

    public boolean isMemberDisabled(MessageReceivedEvent event) {
        return event.isFromType(ChannelType.TEXT) && (disabledMembers.containsKey(event.getGuild().getId()) ? disabledMembers.get(event.getGuild().getId()).contains(event.getAuthor().getId()) : activeQuestionnaires.containsKey(event.getChannel().getId()) && activeQuestionnaires.get(event.getChannel().getId()).contains(event.getAuthor().getId()));
    }

    String getBotOwner() {
        return botOwner;
    }

    List<String> getBotAdmins() {
        return botAdmins;
    }

    List<String> getBotMods() {
        return botMods;
    }

    public void addBotAdmin(String id) {
        if (!botAdmins.contains(id)) botAdmins.add(id);
    }

    public void removeBotAdmin(String id) {
        botAdmins.remove(id);
    }

    public void addBotMod(String id) {
        if (!botMods.contains(id)) botMods.add(id);
    }

    public void removeBotMod(String id) {
        botMods.remove(id);
    }

    @Override
    public void onReady(ReadyEvent event) {

        JDA jda = event.getJDA();
        jda.getGuilds().forEach(guild -> {

            // Start
            Settings settings = Settings.getSettingsOrNull(guild.getId());
            if (settings == null) {
                settings = new Settings(guild.getId());
                settings.defaultSave(); // Register unregistred guild
            }

            if (settings.getPrefix() != null) {
                setCustomPrefix(guild.getId(), prefix);
            }

            if (settings.getLocale() != null) {
                setCustomLocale(guild.getId(), defaultLocale);
            }

        });
    }

}
