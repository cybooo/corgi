package cz.wake.corgibot.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.FinalCommand;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.objects.user.UserGuildData;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatListener extends ListenerAdapter {

    private static final Map<String, Integer> spamMap = new ConcurrentHashMap<>();
    private final Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).build();
    private EventWaiter eventWaiter;

    public ChatListener(EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
    }

    public ChatListener() {
    }

    private static int getGuildUserCount(Guild guild) {
        int i = 0;
        for (Member member : guild.getMembers()) {
            if (!member.getUser().isBot()) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getChannelType() != ChannelType.TEXT) {
            return;
        }

        if (event.getAuthor().isBot()) {
            return;
        }

        if (BotManager.getGuildWrappers() == null) return;

        if (BotManager.getUserWrappers().get(event.getAuthor().getId()) == null) {
            if (!BotManager.loadUser(event.getAuthor().getId())) {
                CorgiBot.getInstance().getSql().registerUser(event.getAuthor().getId(), event.getGuild().getId());
                BotManager.loadUser(event.getAuthor().getId());
            }
        }

        if (BotManager.getUserWrappers().get(event.getAuthor().getId()).getGuildData().get(event.getGuild().getId()) == null) {
            BotManager.getUserWrappers().get(event.getAuthor().getId()).getGuildData().put(event.getGuild().getId(), new UserGuildData(event.getAuthor().getId(), event.getGuild().getId()));
            CorgiBot.getInstance().getSql().registerUser(event.getAuthor().getId(), event.getGuild().getId());
        }

        GuildWrapper guildWrapper;

        if (!CorgiBot.isBeta()) {
            // Custom Guild prefix from SQL
            guildWrapper = BotManager.getCustomGuild(event.getMember().getGuild().getId());

            if (guildWrapper == null) {
                CorgiLogger.warnMessage("Guild " + event.getGuild().getId() + " is still not loaded!");
                return;
            }
        } else {
            // Fake guild
            guildWrapper = new GuildWrapper(event.getGuild().getId()).setPrefix(Constants.PREFIX, false);
        }

        String raw = event.getMessage().getContentRaw();

        try {
            if (raw.startsWith(Constants.PREFIX.toLowerCase()) || raw.startsWith(guildWrapper.getPrefix().toLowerCase())
                    || raw.startsWith(event.getGuild().getSelfMember().getAsMention())) {
                final String[] split = event.getMessage().getContentRaw().replaceFirst(
                        "(?i)" + Pattern.quote(Constants.PREFIX) + "|" + Pattern.quote(guildWrapper.getPrefix()), "").split("\\s+");
                final String invoke = split[0].toLowerCase();

                // Get command
                FinalCommand cmd = CorgiBot.getInstance().getCommandManager().getCommand(invoke);

                if (cmd == null) {
                    return;
                }

                // If Corgi does not own basic permission will do nothing
                if (!event.getGuild().getSelfMember().hasPermission(getBasicPerms())) {
                    return;
                }

                // Check bot owner
                if (cmd.isOnlyOwner() && !event.getAuthor().getId().equals("485434705903222805")) {
                    return;
                }

                // Check if server is beta
                if (cmd.isBeta() && !guildWrapper.isBeta()) {
                    return;
                }

                // Spam detection
                handleSpamDetection(event, guildWrapper, (TextChannel) event.getChannel());

                // Blocking guild
                if (guildWrapper.isBlocked()) {
                    if (System.currentTimeMillis() > guildWrapper.getUnBlockTime() && guildWrapper.getUnBlockTime() != -1) {
                        guildWrapper.revokeBlock();
                    } else {
                        return; // Ignoring blocked guild
                    }
                }

                // Ignored channel
                if (guildWrapper.getIgnoredChannels().contains(event.getChannel()) && !cmd.getName().equalsIgnoreCase("ignore")) {
                    return;
                }

                // Info about sent command
                CorgiLogger.commandMessage("'" + cmd.getCommand() + " " + Arrays.toString(Arrays.copyOfRange(split, 1, split.length)) + "', (Guild: " + event.getGuild().getName() + ", Channel: " + (event.getChannel().getName()) + "), Sender: " + event.getAuthor());

                // Check bot permissions if are required
                if (!event.getGuild().getSelfMember().hasPermission(cmd.getReqBotPermissions())) {
                    StringBuilder sb = new StringBuilder();
                    Arrays.stream(cmd.getReqBotPermissions()).forEach(c -> sb.append(c.name()).append(", "));
                    MessageUtils.sendErrorMessage(I18n.getLoc(guildWrapper, "chat-listener.permission-error-title"), String.format(I18n.getLoc(guildWrapper, "commands.chat-listener.permission-error-description"), sb.substring(0, sb.length() - 2)), event.getChannel());
                    return;
                }

                // Check user permissions if are required
                if (!Objects.requireNonNull(event.getMember()).hasPermission(cmd.getReqUserPermissions())) {
                    CorgiLogger.warnMessage("Command stopped - " + event.getAuthor().getName() + " does not have enough permissions!");
                    return;
                }

                // Run command
                try {
                    cmd.getCommand().onCommand(event.getChannel(), event.getMessage(), Arrays.copyOfRange(split, 1, split.length), event.getMember(), eventWaiter, guildWrapper);
                } catch (Exception ex) {
                    MessageUtils.sendAutoDeletedMessage(I18n.getLoc(guildWrapper, "internal.general.command-failed"), 10000, event.getChannel());
                    ex.printStackTrace();
                }

                // Delete message after
                if (cmd.getCommand().deleteMessage()) {
                    delete(event.getMessage());
                }

                // Statistics
                CorgiBot.commands++;
            } else {
                if (event.getMessage().getContentRaw().length() > 1) {
                    String lastMessage = cache.getIfPresent(event.getAuthor().getId());
                    if (lastMessage == null || !lastMessage.equals(event.getMessage().getContentRaw())) {
                        cache.put(event.getAuthor().getId(), event.getMessage().getContentRaw());
                        BotManager.getUserWrappers().get(event.getAuthor().getId()).getGuildData().get(event.getGuild().getId())
                                .addMessages(1L, true)
                                .addXp(2L, true);
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
            // ¯\_(ツ)_/¯
        } catch (ErrorResponseException ex2) {
            if (ex2.getErrorCode() == 50007) {
                event.getChannel().sendMessage(EmoteList.WARNING + " | " + String.format(I18n.getLoc(guildWrapper, "chat-listener.dant-dm"), event.getAuthor().getAsMention())).queue();
            } else {
                ex2.printStackTrace();
            }
        } catch (NullPointerException ex3) {
            BotManager.registerOrLoadGuild(event.getGuild());
            MessageUtils.sendAutoDeletedMessage(
                    """ 
                            Something went wrong while executing this command!
                            If you just invited Corgi, it's possible that we had some downtime, and your server data was not registered correctly!
                            If this issue persists, or you have been using Corgi already, contact the support!
                            Sorry for the issues :(
                            """, 20000, event.getChannel());
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        CorgiBot.getInstance().getSql().onDisable();
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        if (event.isClosedByServer())
            CorgiLogger.debugMessage(String.format("---- DISCONNECT [SERVER] CODE: [%d] %s%n", event.getServiceCloseFrame()
                    .getCloseCode(), event
                    .getCloseCode()));
        else
            CorgiLogger.debugMessage(String.format("---- DISCONNECT [CLIENT] CODE: [%d] %s%n", event.getClientCloseFrame()
                    .getCloseCode(), event
                    .getClientCloseFrame().getCloseReason()));
    }

    private void delete(Message message) {
        if (message.getChannel().asTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getChannel().asTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    private void handleSpamDetection(MessageReceivedEvent event, GuildWrapper guild, TextChannel ch) {
        if (spamMap.containsKey(event.getGuild().getId())) {

            int messages = spamMap.get(event.getGuild().getId());
            int allowed;

            if (getGuildUserCount(event.getGuild()) < 100) {
                allowed = 10;
            } else {
                allowed = Math.round((float) getGuildUserCount(event.getGuild()) / 10);
            }

            if (messages > allowed) {
                if (!guild.isBlocked()) {
                    MessageUtils.sendErrorMessage(I18n.getLoc(guild, "chat-listener.spam"), ch);
                    guild.setBlocked(true).setBlockReason("Command spam").setUnBlockTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
                }
            } else {
                spamMap.put(event.getGuild().getId(), messages + 1);
            }
        } else {
            spamMap.put(event.getGuild().getId(), 1);
        }
    }

    public void clearSpamMap() {
        spamMap.clear();
    }

    private Permission[] getBasicPerms() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_SEND};
    }
}