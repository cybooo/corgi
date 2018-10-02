package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
/*
@SinceCorgi(version = "1.2")
public class Avatar implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String id;
        if (args.length != 1) {
            id = member.getUser().getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendAutoDeletedMessage("Musíš použít označení s @!", 10000, channel);
            return;
        }
        User user = CorgiBot.getJda().getUserById(id);
        if (user == null) {
            MessageUtils.sendAutoDeletedMessage("Nelze najít uživatele!", 10000, channel);
            return;
        }
        String url = user.getEffectiveAvatarUrl() + "?size=1024";
        channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Avatar uživatele " + user.getName())
                .setImage(url).build()).queue();
    }

    @Override
    public String getCommand() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return "Ziskání profilového obrázku uživatelů.";
    }

    @Override
    public String getHelp() {
        return "%avatar [@nick/ID] - Vygeneruje avatara požadovaného uživatele.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }
}*/
