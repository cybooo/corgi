import listener.MainListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class Main {

    private static Main instance;
    private MainListener events;
    private static JDA jda;

    public static void main(String[] args) throws LoginException, RateLimitedException, InterruptedException {
        System.out.println("Spousteni bota...");

        jda = new JDABuilder(AccountType.BOT)
                .setToken("Mjk0OTUyMTIyNTgyMzAyNzIw.C7cnFQ.wxBtcCBR20t0TMU7vH91Mp6O3cQ")
                .addListener(new MainListener())
                .buildBlocking();
    }


    public static Main getInstance(){
        return instance;
    }

    public MainListener getEvents(){
        return events;
    }

    public static JDA getJda(){
        return jda;
    }
}
