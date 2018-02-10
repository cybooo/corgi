package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Support implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.WHITE).setTitle("Odkaz na server podpory pro Corgiho!")
                .setDescription("K připojení na Discord podpory pro Corgiho použij následující [**ODKAZ**](https://discordapp.com/invite/eaEFCYX)").build()).queue();
    }

    @Override
    public String getCommand() {
        return "support";
    }

    @Override
    public String getDescription() {
        return "Získání odkazu na podporu Corgiho (server).";
    }

    @Override
    public String getHelp() {
        return "%support - Získání odkazu";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"podpora"};
    }
}
