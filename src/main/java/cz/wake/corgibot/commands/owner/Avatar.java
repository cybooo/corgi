package cz.wake.corgibot.commands.owner;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.net.URL;

public class Avatar implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length == 0) {
            if (!message.getAttachments().isEmpty()) {
                Message.Attachment attachment = message.getAttachments().get(0);
                try {
                    sender.getJDA().getSelfUser().getManager().setAvatar(Icon.from(
                            new URL(attachment.getUrl()).openStream()
                    )).complete();
                } catch (IOException e) {
                    MessageUtils.sendErrorMessage("Nepodařilo se nahrát avatar!", channel);
                }
                channel.sendMessage("Úspěšně změněno!!").queue();
            } else {
                MessageUtils.sendErrorMessage("Musíš přiložit obrázek!", channel);
            }
        } else {
            try {
                sender.getJDA().getSelfUser().getManager().setAvatar(Icon.from(
                        new URL(args[0]).openStream()
                )).complete();
            } catch (IOException e) {
                channel.sendMessage("Failed to update avatar!! " + e).queue();
            }
            channel.sendMessage("Úspěšně změněno!!").queue();
        }
    }

    @Override
    public String getCommand() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return "Změna profilovky Corgiho";
    }

    @Override
    public String getHelp() {
        return ".avatar";
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
