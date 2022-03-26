package cz.wake.corgibot.commands.games;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.statuses.MojangChecker;
import cz.wake.corgibot.utils.statuses.MojangService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.concurrent.ConcurrentMap;

@CommandMarker
@SinceCorgi(version = "0.4")
public class McStatusCommand extends ApplicationCommand {

    private final ConcurrentMap<MojangService, Integer> map = MojangChecker.getServiceStatus();

    @JDASlashCommand(
            name = "mcstatus",
            description = "Displays an overview of the Mojang API and its status."
    )
    public void execute(GuildSlashEvent event) {
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
        event.replyEmbeds(builder.setColor((state == 0 ? Constants.GREEN : state == 1 ? Constants.ORANGE : Constants.RED)).build()).queue();
    }

}
