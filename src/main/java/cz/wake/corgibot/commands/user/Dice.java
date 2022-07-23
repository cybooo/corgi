package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ThreadLocalRandom;

@CommandInfo(
        name = "dice",
        description = "commands.dice.description",
        help = "commands.dice.help",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.3.4")
public class Dice implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage(String.format(I18n.getLoc(gw, "commands.dice.rolled"), member.getAsMention(), ThreadLocalRandom.current().nextInt(1, 6))).queue();
    }
}
