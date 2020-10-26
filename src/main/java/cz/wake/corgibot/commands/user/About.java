package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@SinceCorgi(version = "0.1")
public class About implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(message.getContentRaw().contains("corgi")) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setTitle("Představení Corgiho")
                    .setDescription("Ahoj já jsem Corgi, jsem bot pro Discord servery.\nJsem určený pro široké spektrum funkčnosti od administrace, získávání statistik z her až ke klasickým příkazům pro Discord!\n\n" +
                            "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                            "**Invite na tvůj server**: [Odkaz](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)\n" +
                            "**Vytvořil**: " + channel.getJDA().getUserById("177516608778928129").getAsMention() + "\n" +
                            "**Suppport Guild**: [Odkaz](https://discordapp.com/invite/eaEFCYX)")
                    .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl())
                    .setImage("https://cdn.dribbble.com/users/245977/screenshots/4629944/dribbble_1.gif").build()).queue();
        } else {
            channel.sendMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Představení Corgiho")
                    .setDescription("Ahoj já jsem Corgi, jsem bot pro Discord servery.\nJsem určený pro široké spektrum funkčnosti od administrace, získávání statistik z her až ke klasickým příkazům pro Discord!\n\n" +
                            "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                            "**Invite na tvůj server**: [Odkaz](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)\n" +
                            "**Vytvořil**: " + channel.getJDA().getUserById("177516608778928129").getAsMention() + "\n" +
                            "**Suppport Guild**: [Odkaz](https://discordapp.com/invite/eaEFCYX)")
                    .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "about";
    }

    @Override
    public String getDescription() {
        return "Představení Coriho a informace o něm";
    }

    @Override
    public String getHelp() {
        return "%about - Zobrazí základní informace a odkazy pro Corgiho.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"info", "binfo", "corgi"};
    }
}
