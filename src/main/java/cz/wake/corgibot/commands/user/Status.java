package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.statuses.Checker;
import cz.wake.corgibot.utils.statuses.MojangService;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.ConcurrentMap;

@SinceCorgi(version = "0.4")
public class Status implements ICommand {

    private ConcurrentMap map = Checker.getServiceStatus();

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        int state = 0;
        EmbedBuilder builder = new EmbedBuilder();
        for (MojangService service : MojangService.values) {
            String status;
            if (map.containsKey(service)) {
                int time = (int) map.get(service);
                if (time == -1) {
                    status = EmoteList.WARNING + " Výpadky spojení";
                    state = 1;
                } else {
                    status = ":x: Offline (" + time + " minut" + (time < 4 ? "y" : "") + ")";
                    state = 2;
                }
            } else {
                status = ":white_check_mark: Online";
            }
            builder.addField(service.toString(), status, false);
        }
        builder.setThumbnail("http://vgboxart.com/resources/logo/3993_mojang-prev.png");
        channel.sendMessage(builder.setColor((state == 0 ? Constants.GREEN : state == 1 ? Constants.ORANGE : Constants.RED)).build()).queue();
    }

    @Override
    public String getCommand() {
        return "mcstatus";
    }

    @Override
    public String getHelp() {
        return "%mcstatus";
    }

    @Override
    public String getDescription() {
        return "Příkaz na získání základní nápovědy.";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
