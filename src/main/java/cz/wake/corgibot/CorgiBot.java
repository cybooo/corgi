package cz.wake.corgibot;

import cz.wake.corgibot.commands.CommandHandler;
import cz.wake.corgibot.commands.admin.GiveawayCommand;
import cz.wake.corgibot.listener.MainListener;
import cz.wake.corgibot.sql.SQLManager;
import cz.wake.corgibot.utils.LoadingProperties;
import cz.wake.corgibot.utils.statuses.Checker;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;

public class CorgiBot {

    private static CorgiBot instance;
    private MainListener events;
    private static JDA jda;
    private CommandHandler ch = new CommandHandler();
    public static final String PREFIX = ".";
    private SQLManager sql;
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("MMMM yyyy HH:mm:ss");
    private static String imgflipToken = "";

    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException, IOException {
        System.out.println("Spousteni bota...");

        LoadingProperties config = new LoadingProperties();

        jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getBotToken())
                .addEventListener(new MainListener())
                .addEventListener(new GiveawayCommand())
                .setGame(Game.of(".help"))
                .buildBlocking();

        (instance = new CorgiBot()).init();
        (instance = new CorgiBot()).initDatabase();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new Checker(), 10, 60000);

        imgflipToken = config.getImgFlipToken();
    }

    public static CorgiBot getInstance() {
        return instance;
    }

    public MainListener getEvents() {
        return events;
    }

    public static JDA getJda() {
        return jda;
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

    public String getImgflipToken(){
        return imgflipToken;
    }
}
