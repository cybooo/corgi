package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.List;

@CommandInfo(
        name = "love",
        aliases = {"loveme"},
        category = CommandCategory.FUN
)
public class Love implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        List<User> mentioned = message.getMentionedUsers();
        String result;

        if (mentioned.size() < 1) {
            MessageUtils.sendErrorMessage("You need to mention someone! Example: `%love @nick`".replace("%", gw.getPrefix()), channel);
            return;
        }

        long[] ids = new long[2];
        ids[0] = message.getAuthor().getIdLong();
        ids[1] = mentioned.get(0).getIdLong();

        int percentage = (int) (ids[0] == ids[1] ? 101 : (ids[0] + ids[1]) % 101L);

        if (percentage < 45) {
            result = EmoteList.THINKING_1 + " | That won't work.. Match: **" + percentage + "%** between " + message.getAuthor().getAsMention() + " and " + mentioned.get(0).getAsMention();
        } else if (percentage < 75) {
            result = EmoteList.VERIIM + " | Maybe it's gonna work, up to you. Match: **" + percentage + "%** between " + message.getAuthor().getAsMention() + " and " + mentioned.get(0).getAsMention();
        } else if (percentage < 100) {
            result = EmoteList.FEELSOMGYOU + " | This looks like something interesting! Match: **" + percentage + "%** between " + message.getAuthor().getAsMention() + " and " + mentioned.get(0).getAsMention();
        } else {
            result = EmoteList.FEELSSEXMAN + " | I'm suprised.. Go for it! Match: **" + percentage + "%** between " + message.getAuthor().getAsMention() + " and " + mentioned.get(0).getAsMention();
            if (percentage == 101) {
                result = EmoteList.FEELSSTALKERMAN + " | Don't mention yourself! Only you know how much you love yourself!";
            }
        }

        channel.sendMessage(MessageUtils.getEmbed(Color.PINK).setTitle("Love detector :heart:").setDescription(result).build()).queue();

    }

    @Override
    public boolean deleteMessage() {
        return true;
    }

}
