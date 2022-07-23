package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinEvent extends ListenerAdapter {
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        /*
            Initial setup
         */
        BotManager.registerOrLoadGuild(event.getGuild());

        // Logger
        CorgiLogger.infoMessage("GuildJoinEvent - " + event.getGuild().getName() + "(" + event.getGuild().getId() + ")");

        // Informal message
        try {
            MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Corgi joined! :heart_eyes: ")
                    .setDescription("Corgi has joined your server! Change your prefix using `c!prefix [prefix]`. Example: `c!prefix .`\n" +
                            "View all commands using `c!help` or on my [**website**](https://corgibot.xyz)")
                    .setThumbnail(CorgiBot.getShardManager().getShards().get(0).getSelfUser().getAvatarUrl()).setFooter("This message is gonna be deleted in 30 seconds!", null).build(), 40000L, event.getGuild().getDefaultChannel());
        } catch (InsufficientPermissionException ignored) {
        }

        // Info into dev chanel
        if (event.getJDA().getStatus() == JDA.Status.CONNECTED) {
            CorgiBot.getInstance().getGuildLogChannel().sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN)
                    .setThumbnail(event.getGuild().getIconUrl())
                    .setFooter(event.getGuild().getId(), event.getGuild().getIconUrl())
                    .setTitle("Corgi has joined a new guild!")
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl()).setTimestamp(event.getGuild().getSelfMember().getTimeJoined())
                    .setDescription(
                            "Guild name: `" + event.getGuild().getName() + "` :smile: :heart:\n" +
                            "Members: " + event.getGuild().getMembers().size())
                    .build()).queue();
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (BotManager.getUserWrappers().get(event.getUser().getId()) == null) {
            BotManager.loadUser(event.getUser().getId());
        }

        if (!CorgiBot.getInstance().getSql().hasData(event.getUser().getId(), event.getGuild().getId())) {
            CorgiBot.getInstance().getSql().registerUser(event.getUser().getId(), event.getGuild().getId());
            BotManager.loadUser(event.getUser().getId());
        }
    }
}
