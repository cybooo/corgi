package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.ChangeLog;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SinceCorgi(version = "1.3.0")
public class Changelog implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        StringBuilder changelog = new StringBuilder();

        ChangeLog changes = CorgiBot.getInstance().getSql().getLastChanges();

        if(changes == null){
            MessageUtils.sendErrorMessage("Nepodařilo se zjistit poslední změny! Chyba v API.", channel);
            return;
        }

        changelog.append(EmoteList.INFO + " | **Update [" + convertMilisToDate(String.valueOf(changes.getDate())) + "]**");
        changelog.append("\n\n");
        changelog.append(EmoteList.GREEN_OK + "** | Novinky:**\n");
        changelog.append(changes.getNews().replaceAll("-", "•"));
        changelog.append("\n\n");

        if(changes.getFixes() != null){
            changelog.append(EmoteList.BUG + " ** | Opravy & změny:**\n");
            changelog.append(changes.getFixes().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        if(changes.getWarning() != null){
            changelog.append(EmoteList.WARNING + " ** | Upozornění:**\n");
            changelog.append(changes.getWarning().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        changelog.append("**Pro zobrazení starších změn nakoukni do našeho kanálu oznámení v https://discord.gg/eaEFCYX **");

        channel.sendMessage(changelog.toString()).queue();
    }

    @Override
    public String getCommand() {
        return "changelog";
    }

    @Override
    public String getDescription() {
        return "Získej přehled o posledních změnách";
    }

    @Override
    public String getHelp() {
        return "%changelog";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    public static String dateFormat = "dd.MM.yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String convertMilisToDate(String milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }
}
