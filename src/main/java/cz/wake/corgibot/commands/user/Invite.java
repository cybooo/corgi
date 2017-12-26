package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Invite implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Přidání Corgiho na server")
                .setDescription("K pozvání použij následující [**ODKAZ**](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)").build()).queue();
    }

    @Override
    public String getCommand() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Pozvání bota na server";
    }

    @Override
    public String getHelp() {
        return "%invite";
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
