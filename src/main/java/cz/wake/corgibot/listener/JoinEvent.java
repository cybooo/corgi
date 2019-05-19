package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.Set;

public class JoinEvent extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        /*
            Initial setup
         */
        if (CorgiBot.getInstance().getSql().existsGuildData(event.getGuild().getId())) {
            // Load dat from SQL + load into BotManager
            Set<MessageChannel> ignoredChannels = CorgiBot.getInstance().getSql().getIgnoredChannels(event.getGuild().getId());
            GuildWrapper gw = CorgiBot.getInstance().getSql().createGuildWrappers(event.getGuild().getId());
            gw.setIgnoredChannels(ignoredChannels);
            gw.setPrefix("c!", true); // Reset prefixu na c!
            BotManager.addGuild(gw);
        } else {
            // INSERT DAT + insert into BotManager
            CorgiBot.getInstance().getSql().insertDefaultServerData(event.getGuild().getId(), "c!");
            GuildWrapper gw = new GuildWrapper(event.getGuild().getId());
            gw.setPrefix("c!", false);
            BotManager.addGuild(gw);
        }

        // Logger
        CorgiLogger.infoMessage("GuildJoinEvent - " + event.getGuild().getName() + "(" + event.getGuild().getId() + ")");

        // Informal message
        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Corgi je připojen! :heart_eyes: ")
                .setDescription("Corgi byl správně připojen na Váš server. Změň si prefix pomocí příkazu `c!prefix [kod]`. Příklad: `c!prefix .`\n" +
                        "Seznam všech příkazů zobrazíš pomocí `c!help` nebo také na [**WEBU**](https://corgibot.xyz)")
                .setThumbnail(CorgiBot.getJda().getSelfUser().getAvatarUrl()).setFooter("Tato zpráva se smaže sama do 30 vteřin!", null).build(), 40000L, event.getGuild().getDefaultChannel());

        // Info into dev chanel
        if (event.getJDA().getStatus() == JDA.Status.CONNECTED &&
                event.getGuild().getSelfMember().getJoinDate().plusMinutes(2).isAfter(OffsetDateTime.now())) {
            CorgiBot.getInstance().getGuildLogChannel().sendMessage(MessageUtils.getEmbed(Constants.GREEN)
                    .setThumbnail(event.getGuild().getIconUrl())
                    .setFooter(event.getGuild().getId(), event.getGuild().getIconUrl())
                    .setTitle("Corgi se připojil do nové guildy")
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl()).setTimestamp(event.getGuild().getSelfMember().getJoinDate())
                    .setDescription("Název guildy: `" + event.getGuild().getName() + "` :smile: :heart:\n" +
                            "Majitel: " + event.getGuild().getOwner().getUser().getName() + "\nPočet členů: " +
                            event.getGuild().getMembers().size()).build()).queue();
        }
    }
}
