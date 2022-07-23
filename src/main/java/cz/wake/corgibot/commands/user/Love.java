package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
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
        List<User> mentioned = message.getMentions().getUsers();
        String result;

        if (mentioned.size() < 1) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.love.error").replace("%", gw.getPrefix()), channel);
            return;
        }

        long[] ids = new long[2];
        ids[0] = message.getAuthor().getIdLong();
        ids[1] = mentioned.get(0).getIdLong();

        int percentage = (int) (ids[0] == ids[1] ? 101 : (ids[0] + ids[1]) % 101L);

        if (percentage < 45) {
            result = EmoteList.THINKING_1 + " | " + I18n.getLoc(gw, "commands.love.p45") + String.format(I18n.getLoc(gw, "commands.love.match"), percentage, message.getAuthor().getAsMention(), mentioned.get(0).getAsMention());
        } else if (percentage < 75) {
            result = EmoteList.VERIIM + " | " + I18n.getLoc(gw, "commands.love.p75") + String.format(I18n.getLoc(gw, "commands.love.match"), percentage, message.getAuthor().getAsMention(), mentioned.get(0).getAsMention());
        } else if (percentage < 100) {
            result = EmoteList.FEELSOMGYOU + " | " + I18n.getLoc(gw, "commands.love.p100") + String.format(I18n.getLoc(gw, "commands.love.match"), percentage, message.getAuthor().getAsMention(), mentioned.get(0).getAsMention());
        } else {
            result = EmoteList.FEELSSEXMAN + " | " + I18n.getLoc(gw, "commands.love.p999") + String.format(I18n.getLoc(gw, "commands.love.match"), percentage, message.getAuthor().getAsMention(), mentioned.get(0).getAsMention());
            if (percentage == 101) {
                result = EmoteList.FEELSSTALKERMAN + " | " + I18n.getLoc(gw, "commands.love.mention-yourself");
            }
        }

        channel.sendMessageEmbeds(MessageUtils.getEmbed(Color.PINK).setTitle(I18n.getLoc(gw, "commands.love.detector")).setDescription(result).build()).queue();

    }

    @Override
    public boolean deleteMessage() {
        return true;
    }

}
