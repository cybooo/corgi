package cz.wake.corgibot.listener;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class MainListener extends ListenerAdapter {

    private EventWaiter w;

    public MainListener(EventWaiter w) {
        this.w = w;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        if (e.getAuthor().isBot() || e.getAuthor().isFake() || e.getAuthor() == null) {
            return;
        }

        if (BotManager.getListGuilds() == null) return;

        // Custom Guild prefix
        GuildWrapper guildWrapper = BotManager.getCustomGuild(e.getMember().getGuild().getId());
        String prefix = guildWrapper.getPrefix();

        if (e.getMessage().getContentRaw().startsWith(prefix) || e.getMessage().getContentRaw().substring(0,prefix.length()).contains(prefix)) {
            String message = e.getMessage().getContentRaw();
            String command = message.substring(prefix.length());
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, message.indexOf(" ") - prefix.length());
                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            for (ICommand cmd : CorgiBot.getInstance().getCommandHandler().getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command) || Arrays.asList(cmd.getAliases()).contains(command)) {
                    if (guildWrapper.getIgnoredChannels().contains(e.getChannel()) && !cmd.getCommand().equalsIgnoreCase("ignore")) {
                        return;
                    }
                    String[] finalArgs = args;
                    CorgiBot.LOGGER.info("Provádění příkazu '" + cmd.getCommand() + "' " + Arrays
                            .toString(args) + " v G:" + e.getGuild().getName() + " (" + (e.getChannel().getName()) + ")! Odeslal: " +
                            e.getAuthor() + '#' + e.getAuthor().getDiscriminator());
                    List<Permission> perms = e.getGuild().getSelfMember().getPermissions(e.getChannel());
                    if (!perms.contains(Permission.MESSAGE_EMBED_LINKS)) {
                        e.getChannel().sendMessage(":warning: | Nemám dostatečná práva na používání EMBED odkazů! Přiděl mi právo: `Vkládání odkazů` nebo `Embed Links`.").queue();
                        return;
                    }
                    if (Rank.getPermLevelForUser(e.getAuthor(), e.getChannel()).isAtLeast(cmd.getRank())) {
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w, guildWrapper);
                        } catch (Exception ex) {
                            MessageUtils.sendAutoDeletedMessage("Interní chyba při provádění příkazu!", 10000, e.getChannel());
                            CorgiBot.LOGGER.error("Chyba při provádění příkazu '" + cmd.getCommand() + "' " + Arrays
                                    .toString(args) + " v G:" + e.getGuild().getName() + " (" + (e.getChannel().getName()) + ")! Odeslal: " +
                                    e.getAuthor() + '#' + e.getAuthor().getDiscriminator(), ex);
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    }
                    CorgiBot.commands++;

                }
            }
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        CorgiBot.getInstance().getSql().onDisable();
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        if (event.isClosedByServer())
            CorgiBot.LOGGER.error(String.format("---- DISCONNECT [SERVER] CODE: [%d] %s%n", event.getServiceCloseFrame()
                    .getCloseCode(), event
                    .getCloseCode()));
        else
            CorgiBot.LOGGER.error(String.format("---- DISCONNECT [CLIENT] CODE: [%d] %s%n", event.getClientCloseFrame()
                    .getCloseCode(), event
                    .getClientCloseFrame().getCloseReason()));
    }

    private void delete(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        MessageUtils.sendAutoDeletedMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Corgi je připojen! :heart_eyes: ")
                .setDescription("Corgi byl správně připojen na Váš server. Pokud chceš změnit prefix, použij příkaz `.prefix`\n" +
                        "Seznam všech příkazů zobrazíš pomocí `.help` nebo také na [**WEBU**](http://corgibot.xyz)")
                .setThumbnail(CorgiBot.getJda().getSelfUser().getAvatarUrl()).setFooter("Tato zpráva se smaže sama do 30 vteřin!", null).build(), 40000L, event.getGuild().getDefaultChannel());

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

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        CorgiBot.getInstance().getGuildLogChannel().sendMessage(MessageUtils.getEmbed(Constants.RED)
                .setThumbnail(event.getGuild().getIconUrl())
                .setFooter(event.getGuild().getId(), event.getGuild().getIconUrl())
                .setTimestamp(OffsetDateTime.now())
                .setTitle("Corgi byl vyhozen z guildy")
                .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                .setDescription("Nazev guildy: `" + event.getGuild().getName() + "` :broken_heart:\n" +
                        "Majitel: " + (event.getGuild().getOwner() != null ?
                        event.getGuild().getOwner().getUser().getName()
                        : "Neexistuje, nebo nelze zjistit!")).build()).queue();
        // Smazani vsech ignored IDs
        CorgiBot.getInstance().getSql().deleteIgnoredChannel(event.getGuild().getId());
    }

    private String getGuildId(GenericGuildMessageEvent e) {
        return e.getChannel().getGuild() != null ? e.getChannel().getGuild().getId() : null;
    }
}