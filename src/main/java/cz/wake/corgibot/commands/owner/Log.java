package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.io.File;

@SinceCorgi(version = "1.0")
public class Log implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if(args.length < 1){
            try {
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Vygenerovaný log (latest.log) by vyžádán!").build()).queue();
                File log = new File("latest.log");
                channel.sendFile(log,"latest.log").queue();
            } catch (Exception e){
                //
            }
        }
    }

    @Override
    public String getCommand() {
        return "log";
    }

    @Override
    public String getDescription() {
        return "Zaslání logu";
    }

    @Override
    public String getHelp() {
        return "%log";
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }

    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
