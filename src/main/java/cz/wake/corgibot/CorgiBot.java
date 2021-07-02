package cz.wake.corgibot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.commands.CommandHandler;
import cz.wake.corgibot.feeds.TwitterEventListener;
import cz.wake.corgibot.listener.ChannelDeleteEvent;
import cz.wake.corgibot.listener.ChatListener;
import cz.wake.corgibot.listener.JoinEvent;
import cz.wake.corgibot.listener.LeaveEvent;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.runnable.ReminderTask;
import cz.wake.corgibot.runnable.SpamHandler;
import cz.wake.corgibot.sql.SQLManager;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.config.Config;
import cz.wake.corgibot.utils.config.ConfigUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.statuses.MojangChecker;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class CorgiBot {

    private static CorgiBot instance;
    private static JDA jda;
    private final CommandHandler ch = new CommandHandler();
    private SQLManager sql;
    private ChatListener chatListener;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("MMMM yyyy HH:mm:ss");
    public static long startUp;
    private static final Map<String, Logger> LOGGERS;
    public static final Logger LOGGER;
    public static int commands = 0;
    private static boolean isBeta = true;
    public static final Config config = new ConfigUtils().loadConfig();

    static {
        new File("logs/latest.log").renameTo(new File("logs/log-" + getCurrentTimeStamp() + ".log"));
        LOGGERS = new ConcurrentHashMap<>();
        LOGGER = getLog(CorgiBot.class);
        new File("resources/feeds").mkdirs();
    }

    public static void main(String[] args) throws LoginException, InterruptedException {

        // Inform
        CorgiLogger.infoMessage("Now wil Corgi wake up!");

        // Logo on start
        bootLogo();

        // JDA Event Waiter
        EventWaiter waiter = new EventWaiter();

        // Startup time
        startUp = System.currentTimeMillis();

        // JDA Build
        CorgiLogger.infoMessage("Connecting to Discord API.");
        jda = JDABuilder.createDefault(config.getString("discord.token"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new ChatListener(waiter))
                .addEventListeners(new LeaveEvent())
                .addEventListeners(new JoinEvent())
                .addEventListeners(new ChannelDeleteEvent())
                .addEventListeners(waiter)
                .setActivity(Activity.playing("Loading..."))
                .setStatus(OnlineStatus.IDLE)
                .build().awaitReady();

        // Instances
        (instance = new CorgiBot()).init();

        // Properties from config
        isBeta = config.getBoolean("beta");

        // MySQL
        if(!isBeta){
            CorgiLogger.infoMessage("Connection to MySQL...");
            try {
                // MySQL Instance
                (instance = new CorgiBot()).initDatabase();
                CorgiLogger.greatMessage("Corgi is successful connected to MySQL.");

                // Load configuration for guilds
                BotManager.loadGuilds();

                // Setup
                isBeta = false;

            } catch (Exception ex){
                CorgiLogger.dangerMessage("During connection to MySQL, error has occurred:");
                ex.printStackTrace();
                System.exit(-1);
            }
        } else {
            CorgiLogger.warnMessage("Database is off, Corgi will not load and save anything!");
            CorgiLogger.infoMessage("Basic prefix is: " + Constants.PREFIX);
        }

        // Startup timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SpamHandler(), 10, 1500); // 1.5s clear, higher = disaster

        // Languages
        CorgiLogger.infoMessage("Loading language files...");
        I18n.start();

        // Is Corgi beta?
        if (!isBeta) {
            CorgiLogger.infoMessage("Corgi will run as PRODUCTION bot.");
            timer.scheduleAtFixedRate(new MojangChecker(), 10, 60000);
            timer.scheduleAtFixedRate(new ReminderTask(getInstance()), 10, 20000);
            TwitterEventListener.initTwitter();
        } else {
            CorgiLogger.warnMessage("Corgi is running as BETA bot! Some functions will not work!");
        }

        // Setup new profile image from config.json
        if(config.getBoolean("advanced.profile-picture.enabled")){
            try {
                String url = config.getString("advanced.profile-picture.url");
                jda.getSelfUser().getManager().setAvatar(Icon.from(
                        new URL(url).openStream())).complete();
                CorgiLogger.greatMessage("New profile image has been set from: " + url);
            } catch (IOException e) {
                CorgiLogger.dangerMessage("Error when Corgi setup image:");
                e.printStackTrace();
            }
        }

        // Final set status
        if (!isBeta) {
            getJda().getPresence().setActivity(Activity.playing("c!help | corgibot.xyz"));
            getJda().getPresence().setStatus(OnlineStatus.ONLINE);
        } else {
            getJda().getPresence().setActivity(Activity.playing("with bugs"));
            getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        }
    }

    public static CorgiBot getInstance() {
        return instance;
    }

    public static JDA getJda() {
        return jda;
    }

    public ChatListener getChatListener(){
        return chatListener;
    }

    public CommandHandler getCommandHandler() {
        return ch;
    }

    public SQLManager getSql() {
        return sql;
    }

    private void init() {
        ch.register();
    }

    private void initDatabase() {
        sql = new SQLManager(this);
    }

    public String formatTime(LocalDateTime dateTime) {
        return dateTime.getDayOfMonth() + ". " + dateTime.format(timeFormat);
    }

    private static Logger getLog(String name) {
        return LOGGERS.computeIfAbsent(name, LoggerFactory::getLogger);
    }

    public static Logger getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public TextChannel getGuildLogChannel() {
        return getJda().getGuildById("860251548231532584").getTextChannelById("860299812582981643");
    }

    public static Guild getDefaultGuild() {
        return getJda().getGuildById("860251548231532584");
    }

    private static void bootLogo() {
        LOGGER.info("");
        LOGGER.info("       ______                 _ ");
        LOGGER.info("      / ____/___  _________ _(_)");
        LOGGER.info("     / /   / __ \\/ ___/ __ `/ / ");
        LOGGER.info("    / /___/ /_/ / /  / /_/ / /  ");
        LOGGER.info("    \\____/\\____/_/   \\__, /_/   ");
        LOGGER.info("                    /____/      ");
        LOGGER.info("");
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public static Config getConfig() {
        return config;
    }

    /*
        Whatever is Corgi in BETA state!
     */
    public static boolean isIsBeta() {
        return isBeta;
    }
}
