package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.*;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class HelpCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        sender.openPrivateChannel().queue(msg -> {
            msg.sendMessage(MessageUtils.getEmbed(Constants.BLUE)
                    .setTitle("**Nápověda k CorgiBot**", null)
                    .setDescription("Seznam všech registrovaných příkazů.")
                    .addBlankField(false)
                    .addField("Hlavní příkazy", "Příkazy na používání pro všechny uživatele\n", false)
                    .addField("\u200B","[.help]()\n[.uptime]()\n[.userinfo]()\n[.git]()\n[.ping]()", true)
                    .addField("\u200B","[.status]()\n[.pstats]()\n[.bstats]()", true)
                    .addBlankField(true)
                    .addBlankField(false)
                    .addField("Fun příkazy", "Příkazy, které pobaví a zabaví všechny", false)
                    .addField("\u200B","[.8ball]()\n[.fact]()\n[.meme]()\n[.ttb]()\n[.trump]()", true)
                    .addBlankField(true)
                    .addBlankField(true)
                    .addBlankField(false)
                    .addField("Moderátor příkazy", "Příkazy určené na moderování a správu Discord serveru", false)
                    .addField("\u200B","[.purge]()\n[.roles]()\n[.archive]()\n[.giveaway]()\n[.emote]()", true)
                    .addField("\u200B", "[.say]()\n[.ats]()", true)
                    .addBlankField(true)
                    .build()).queue();
        });
    }

    //TODO: Dodelat jednotne a automaticky

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Základní nápověda pro Corgiho.";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.ALL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

}
