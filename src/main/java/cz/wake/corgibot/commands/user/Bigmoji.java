package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Bigmoji implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Help" + " - bigmoji :question:")
                    .setDescription(getDescription() + "\n\n**Usage**\n" + //TODO: Translate
                            getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) {
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                long longId = Long.parseLong(id);
                Emote emote = channel.getJDA().getEmoteById(longId);
                if (emote != null) {
                    channel.sendMessage(MessageUtils.getEmbed().setImage(emote.getImageUrl()).build()).queue();
                } else {
                    MessageUtils.sendErrorMessage("You can only use emojis from this server!", channel);
                }
            } else {
                MessageUtils.sendErrorMessage("Invalid emoji format! Try again!", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "bigmoji";
    }

    @Override
    public String getDescription() {
        return "Generating big emojis from server.";
    }

    @Override
    public String getHelp() {
        return "%bigmoji <regex|text> - Will send big emoji into channel.";
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
