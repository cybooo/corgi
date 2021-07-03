package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "fact",
        aliases = {"fakt"},
        description = "Facts every day.",
        help = "%fact - Generate random fact.",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "2.3.2")
public class Fact implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage("This command is currently unavailable!").queue();
        //channel.sendMessage(MessageUtils.getEmbed(Constants.PINK).setTitle("Random fact :trophy:", null).setDescription(CorgiBot.getInstance().getSql().getRandomFact()).build()).queue();
    }

}
