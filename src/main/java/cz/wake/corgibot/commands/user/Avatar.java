package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.net.URL;

public class Avatar implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        String id;
        if (args.length != 1) {
            id = sender.getId();
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
        return "%avatar [@nick/ID]";
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
