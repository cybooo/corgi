package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Random;

@SinceCorgi(version = "3.3.0")
public class Choose implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            MessageUtils.sendErrorMessage("Musíš si něco vybrat!", channel);
        } else {
            // Format message
            String request = message.getContentRaw().replaceAll("\\s+\\|", "|").replaceAll("\\|\\s+", "|").replaceAll("\\|", "|").replace("choose ", "").replace("volba ", "").replace(gw.getPrefix(), "");
            String[] arguments = request.split("\\|");
            if (arguments.length == 1) {
                MessageUtils.sendErrorMessage("Musíš zadat víc než 1 volbu!", channel);
                return;
            }
            if (arguments[0].equalsIgnoreCase("choose") || arguments[0].equalsIgnoreCase("volba")) {
                MessageUtils.sendErrorMessage("První možnost byla zadána špatně. Zkus to znova...", channel);
                return;
            }
            channel.sendMessage(getRandomThinkingEmote() + " | **" + member.getUser().getName() + "**, zvolil jsem **" + arguments[(int) (Math.random() * arguments.length)] + "**!").queue();
        }
    }

    @Override
    public String getCommand() {
        return "choose";
    }

    @Override
    public String getDescription() {
        return "Nevíš co? Nech Corgiho ať rozhodne za tebe.";
    }

    @Override
    public String getHelp() {
        return "%choose volba1 | volba2 | volba3 - ukázka příkazu";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"volba"};
    }

    private String getRandomThinkingEmote() {
        Random r = new Random();
        int number = r.nextInt(3) + 1;
        switch (number) {
            case 1:
                return EmoteList.THINKING_1;
            case 2:
                return EmoteList.THINKING_2;
            case 3:
                return EmoteList.THINKING_3;
            default:
                return EmoteList.THINKING_1;
        }
    }
}
