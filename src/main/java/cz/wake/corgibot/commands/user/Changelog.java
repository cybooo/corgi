package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.ChangeLog;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@CommandInfo(
        name = "changelog",
        description = "commands.changelog.description",
        help = "commands.changelog.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.3.0")
public class Changelog implements CommandBase {

    public static final String dateFormat = "dd.MM.yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String convertMilisToDate(String milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return simpleDateFormat.format(calendar.getTime());
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        StringBuilder changelog = new StringBuilder();

        ChangeLog changes = CorgiBot.getInstance().getSql().getLastChanges();

        if (changes == null) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.api-failed"), channel);
            return;
        }

        changelog.append(EmoteList.INFO + " | **" + I18n.getLoc(gw, "commands.changelog.update") + " [").append(convertMilisToDate(String.valueOf(changes.getDate()))).append("]**");
        changelog.append("\n\n");
        if (changes.getNews() != null) {
            changelog.append(EmoteList.GREEN_OK + "** | ").append(I18n.getLoc(gw, "commands.changelog.news")).append(":**\n");
            changelog.append(changes.getNews().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        if (changes.getFixes() != null) {
            changelog.append(EmoteList.BUG + " ** | ").append(I18n.getLoc(gw, "commands.changelog.fixes")).append(":**\n");
            changelog.append(changes.getFixes().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        if (changes.getWarning() != null) {
            changelog.append(EmoteList.WARNING + " ** | ").append(I18n.getLoc(gw, "commands.changelog.announce")).append(":**\n");
            changelog.append(changes.getWarning().replaceAll("-", "•"));
            changelog.append("\n\n");
        }

        changelog.append(I18n.getLoc(gw, "commands.changelog.footer").replace("{1}", "https://discord.gg/pR2tj432NS"));

        channel.sendMessage(changelog.toString()).queue();
    }
}
