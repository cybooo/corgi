package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class HelpCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(channel.getType().equals(ChannelType.TEXT)){
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
        } else {
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
    }

    //TODO: Dodelat jednotne a automaticky

    @Override
    public String getCommand() {
        return "help";
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
