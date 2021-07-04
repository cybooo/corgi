package cz.wake.corgibot.commands.games;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.statuses.MojangChecker;
import cz.wake.corgibot.utils.statuses.MojangService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentMap;

@CommandInfo(
        name = "mcstatus",
        aliases = {"mcs", "mojang"},
        help = "%mcstatus - Displays an overview of the Mojang API and its status.",
        description = "Command to view Mojnag status.",
        category = CommandCategory.GAMES
)
@SinceCorgi(version = "0.4")
public class McStatus implements CommandBase {

    private final ConcurrentMap<MojangService, Integer> map = MojangChecker.getServiceStatus();

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        int state = 0;
        EmbedBuilder builder = new EmbedBuilder();
        for (MojangService service : MojangService.values) {
            String status;
            if (map.containsKey(service)) {
                int time = map.get(service);
                if (time == -1) {
                    status = EmoteList.WARNING + " Connection failures";
                    state = 1;
                } else {
                    status = ":x: Offline (" + time + " minutes" + (time < 4 ? "y" : "") + ")";
                    state = 2;
                }
            } else {
                status = ":white_check_mark: Online";
            }
            builder.addField(service.toString(), status, false);
        }
        builder.setThumbnail("https://boldscandinavia.com/wp-content/uploads/2020/05/moj_hor_1080x1080_compressed.gif");
        builder.setFooter("Some services may be offline because Mojang status marked them as offline.");
        channel.sendMessage(builder.setColor((state == 0 ? Constants.GREEN : state == 1 ? Constants.ORANGE : Constants.RED)).build()).queue();
    }
}
