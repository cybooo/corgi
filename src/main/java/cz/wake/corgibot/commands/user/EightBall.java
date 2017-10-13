package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.util.concurrent.ThreadLocalRandom;

public class EightBall implements ICommand {

    static String outcomes[] = {"Ano.", "Ne.", "S největší pravděpodobností ANO!", "Možná.", "Počkej zamyslím se, ANO!", "Pravděpodobně ne!", "Nepravděpodobně...", "Když se nad tím zamyšlíš, je to možné!", "Je to jistý.", "Je to rozhodně tak", "Definitivně ano", "Něco mi říká, že ne"};

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        try {
            if (args.length < 1) {
                channel.sendMessage(sender.getAsMention() + " Musíš položit otázku, neumím číst myšlenky!").queue();
            } else {
                channel.sendMessage(MessageUtils.getEmbed(sender, Constants.PINK).addField(sender.getName() + " se ptá:", message.getRawContent().replace(".8ball ", "").replace(".8b", ""), false).addField("Corgi odpovídá:", outcomes[ThreadLocalRandom.current().nextInt(0, outcomes.length)], false).build()).queue();
            }
        } catch (Exception e) {
            MessageUtils.sendAutoDeletedMessage("Chyba při provádění příkazu!", 10000, channel);
            CorgiBot.LOGGER.error("Chyba při provádění příkazu .8ball!", e);
        }
    }

    @Override
    public String getCommand() {
        return "8ball";
    }

    @Override
    public String getDescription() {
        return "Zeptej se zda to je pravda nebo ne!";
    }

    @Override
    public String getHelp() {
        return ".8ball <otázka>";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
