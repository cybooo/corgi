package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Animoji implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1) {
            //HELP
        } else {

        }
    }

    @Override
    public String getCommand() {
        return "animoji";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
