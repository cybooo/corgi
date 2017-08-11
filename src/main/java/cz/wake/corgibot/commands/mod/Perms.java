package cz.wake.corgibot.commands.mod;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Perms implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Nastavení práv pro Corgiho")
                .setDescription("Ke správné funkčnosti práv na Discord serveru,\nje zapotřebí, aby práva byly nastaveny následovně.\n\n" +
                        ":white_medium_small_square: **Administrator** - Práva Administratora Discord serveru\n" +
                        ":white_medium_small_square: **Moderator** - Právo na `BAN_PLAYERS`\n" +
                        ":white_medium_small_square: **Uživatel** - Všichni ostatní\n\n" +
                        "Zjištění aktuálních práv pro Corgiho - .perms @nick").build()).queue();
        } else {
            String id = args[0].replaceAll("[^0-9]", "");
            if (id.isEmpty()) {
                MessageUtils.sendErrorMessage("Musíš použít označení s @!", channel);
                return;
            }
            User user = CorgiBot.getJda().getUserById(id);
            if (user == null) {
                MessageUtils.sendErrorMessage("Nelze najít uživatele!", channel);
                return;
            }
            Rank rank = Rank.getPermLevelForUser(user, message.getTextChannel());
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(user.getAsMention() + " má rank: **" + rank.formattedName() + "**").build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "perms";
    }

    @Override
    public String getDescription() {
        return "Informace o tom jak nastavit práva na serveru, aby Corgi fungoval správně.";
    }

    @Override
    public String getHelp() {
        return ".perms - Jak na práva\n" +
                ".perms @nick - Zjištění aktuální skupiny";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
