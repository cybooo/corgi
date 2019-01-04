package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Bigmoji implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - bigmoji :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) {
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                Long longId = Long.valueOf(id);
                Emote emote = channel.getJDA().getEmoteById(longId);
                if (emote != null) {
                    channel.sendMessage(MessageUtils.getEmbed().setImage(emote.getImageUrl()).build()).queue();
                } else {
                    MessageUtils.sendErrorMessage("Lze používat pouze emoji na tomto serveru!", channel);
                }
            } else {
                MessageUtils.sendErrorMessage("Neplatný formát emoji! Zkus to znova!", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "bigmoji";
    }

    @Override
    public String getDescription() {
        return "Generování velkých emoji v chatu!";
    }

    @Override
    public String getHelp() {
        return "%bugmoji <regex|text> - K odeslání velkého emoji!";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"animoji"};
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
