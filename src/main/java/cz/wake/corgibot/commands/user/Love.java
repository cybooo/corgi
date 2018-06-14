package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;
import java.util.List;

public class Love implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        List<User> mentioned = message.getMentionedUsers();
        String result;

        if(mentioned.size() < 1) {
            MessageUtils.sendErrorMessage("Musíš označit aspoň jednu osobu! Př. `%love @nick`".replace("%", gw.getPrefix()), channel);
            return;
        }

        long[] ids = new long[2];
        ids[0] = message.getAuthor().getIdLong();
        ids[1] = mentioned.get(0).getIdLong();

        int percentage = (int)(ids[0] == ids[1] ? 101 : (ids[0] + ids[1]) % 101L);

        if(percentage < 45) {
            result = EmoteList.THINKING_1 + " | To bohužel nepůjde... Shoda: **" + percentage + "%** mezi " + message.getAuthor().getAsMention() + " a " + mentioned.get(0).getAsMention();
        } else if(percentage < 75) {
            result = EmoteList.VERIIM + " | Možná to půjde, záleží na vás... Shoda: **" + percentage + "%** mezi " + message.getAuthor().getAsMention() + " a " + mentioned.get(0).getAsMention();
        } else if(percentage < 100) {
            result = EmoteList.FEELSOMGYOU + " | Tady to vypadá na něco zajímavějšího... Shoda: **" + percentage + "%** mezi " + message.getAuthor().getAsMention() + " a " + mentioned.get(0).getAsMention();
        } else {
            result = EmoteList.FEELSSEXMAN + " | Tak to žasnu, jděte na to... Shoda: **" + percentage + "%** mezi " + message.getAuthor().getAsMention() + " a " + mentioned.get(0).getAsMention();
            if(percentage == 101) {
                result = EmoteList.FEELSSTALKERMAN +  " | Povím ti tajemství, tak blbý nejsem! Neoznačuj sám sebe, pouze ty víš jak moc se máš rád/a.";
            }
        }

        channel.sendMessage(MessageUtils.getEmbed(Color.PINK).setTitle("Detektor lásky :heart:").setDescription(result).build()).queue();

    }

    @Override
    public String getCommand() {
        return "love";
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
        return new String[]{"laska","loveme"};
    }
}
