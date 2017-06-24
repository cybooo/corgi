package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class HelpCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Zkontroluj si zprávy", null).setDescription(":mailbox_with_mail: | Odeslal jsem ti do zpráv nápovědu s příkazy!").build()).queue();
        sender.openPrivateChannel().queue(msg -> {
            msg.sendMessage(MessageUtils.getEmbed(sender).setColor(Constants.BLUE)
                    .setTitle("**Nápověda k CorgiBot (ALPHA)**", null).setDescription(
                            "**.git** - Odkaz na můj source\n" +
                                    "**.8ball [otázka]** - Zkouška pravdy ANO/NE\n" +
                                    "**.ping** - Zkouška pingu\n" +
                                    "**.fact** - Náhodné fakty\n" +
                                    "**.userinfo [nick]** - Zobrazení informací o uživateli\n" +
                                    "**.uptime** - Zobrazí čas od spuštění\n" +
                                    "**.meme** - Generátor meme obrázků\n" +
                                    "**.emote [emote]** - Informace o emotes\n" +
                                    "**.status** - Mojang Status\n" +
                                    "**.ps [nick]** - Zobrazí statistiky ze serveru Craftmania (ALPHA)").build()).queue();
        });
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Vypis vsech prikazu.";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
