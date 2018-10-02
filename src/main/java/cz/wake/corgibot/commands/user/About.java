package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.CommandEvent;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;

public class About extends Command {

    public About() {
        this.name = "about";
        this.description = "Představení Coriho a informace o něm";
        this.category = new Category(CommandCategory.GENERAL);
        this.usage.add("about - Zobrazí základní informace a odkazy pro Corgiho.");
        this.aliases = new String[]{"info", "binfo", "corgi"};
    }

    @Override
    public void onExecuted(CommandEvent event) throws Throwable {
        if (event.getMessage().getContentRaw().contains("corgi")) {
            event.getChannel().sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Představení Corgiho")
                    .setDescription("Ahoj já jsem Corgi, jsem bot pro Discord servery.\nJsem určený pro široké spektrum funkčnosti od administrace, získávání statistik z her až ke klasickým příkazům pro Discord!\n\n" +
                            "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                            "**Invite na tvůj server**: [Odkaz](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)\n" +
                            "**Vytvořil**: " + event.getChannel().getJDA().getUserById("177516608778928129").getAsMention() + "\n" +
                            "**Suppport Guild**: [Odkaz](https://discordapp.com/invite/eaEFCYX)")
                    .setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl())
                    .setImage("https://cdn.dribbble.com/users/245977/screenshots/4629944/dribbble_1.gif").build()).queue();
        } else {
            event.getChannel().sendMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Představení Corgiho")
                    .setDescription("Ahoj já jsem Corgi, jsem bot pro Discord servery.\nJsem určený pro široké spektrum funkčnosti od administrace, získávání statistik z her až ke klasickým příkazům pro Discord!\n\n" +
                            "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                            "**Invite na tvůj server**: [Odkaz](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)\n" +
                            "**Vytvořil**: " + event.getChannel().getJDA().getUserById("177516608778928129").getAsMention() + "\n" +
                            "**Suppport Guild**: [Odkaz](https://discordapp.com/invite/eaEFCYX)")
                    .setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl()).build()).queue();
        }
    }
}
