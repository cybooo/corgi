package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.ChangeLog;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SinceCorgi(version = "1.3.0")
public class Changelog implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        StringBuilder changelog = new StringBuilder();

        ChangeLog changes = CorgiBot.getInstance().getSql().getLastChanges();

        if (changes == null) {
            MessageUtils.sendErrorMessage("Something went wrong! Try again later..", channel);
            return;
        }

        changelog.append(EmoteList.INFO + " | **Update [").append(convertMilisToDate(String.valueOf(changes.getDate()))).append("]**");
        changelog.append("\n\n");
        if (changes.getNews() != null) {
            changelog.append(EmoteList.GREEN_OK + "** | " + "News" + ":**\n");
            changelog.append(changes.getNews().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        if (changes.getFixes() != null) {
            changelog.append(EmoteList.BUG + " ** | " + "Fixes & Changes" + ":**\n");
            changelog.append(changes.getFixes().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        if (changes.getWarning() != null) {
            changelog.append(EmoteList.WARNING + " ** | " + "Warning" + ":**\n");
            changelog.append(changes.getWarning().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        changelog.append("Do you want see old changes? Look at our changelog channel on support guild!".replace("{1}", "https://discord.gg/pR2tj432NS"));

        channel.sendMessage(changelog.toString()).queue();
    }

    @Override
    public String getCommand() {
        return "changelog";
    }

    @Override
    public String getDescription() {
        return "Latest changes and news, what has been changed in Corgi";
    }

    @Override
    public String getHelp() {
        return "%changelog - Generates latest changelog";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    public static String dateFormat = "dd.MM.yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String convertMilisToDate(String milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }
}
