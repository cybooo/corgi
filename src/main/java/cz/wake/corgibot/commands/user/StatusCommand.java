package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.statuses.Checker;
import cz.wake.corgibot.utils.statuses.MojangService;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.concurrent.ConcurrentMap;

public class StatusCommand implements Command {

    private ConcurrentMap map = Checker.getServiceStatus();

    //TODO: Dodělat pro CM

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        int state = 0;
        EmbedBuilder builder = new EmbedBuilder();
        for (MojangService service : MojangService.values) {
            String status;
            if (map.containsKey(service)) {
                int time = (int) map.get(service);
                if (time == -1) {
                    status = ":warning: Výpadky spojení";
                    state = 1;
                } else {
                    status = ":x: Offline (" + time + " minut" + (time < 4 ? "y" : "") + "";
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
        return "status";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
