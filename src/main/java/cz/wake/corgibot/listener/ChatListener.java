package cz.wake.corgibot.listener;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.metrics.Metrics;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatListener extends ListenerAdapter {

    private EventWaiter w;
    private static Map<String, Integer> spamMap = new ConcurrentHashMap<>();

    public ChatListener(EventWaiter w) {
        this.w = w;
    }

    public ChatListener() {
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        if (e.getAuthor().isBot() || e.getAuthor().isFake()) {
            return;
        }

        if (BotManager.getListGuilds() == null) return;

        String prefix;
        GuildWrapper guildWrapper;

        if (!CorgiBot.isIsBeta()) {
            // Custom Guild prefix from SQL
            guildWrapper = BotManager.getCustomGuild(e.getMember().getGuild().getId());
            if(guildWrapper.getPrefix() != null) {
                prefix = guildWrapper.getPrefix();
            }
        } else {
            // Fake guild
            prefix = Constants.PREFIX;
            guildWrapper = new GuildWrapper(e.getGuild().getId()).setPrefix(prefix, false);
        }

        String raw = e.getMessage().getContentRaw();

        try {
            if (raw.startsWith(Constants.PREFIX.toLowerCase()) || raw.startsWith(guildWrapper.getPrefix().toLowerCase())
                    || raw.startsWith(e.getGuild().getSelfMember().getAsMention())) {
                final String[] split = e.getMessage().getContentRaw().replaceFirst(
                        "(?i)" + Pattern.quote(Constants.PREFIX) + "|" + Pattern.quote(guildWrapper.getPrefix()), "").split("\\s+");
                final String invoke = split[0].toLowerCase();

                // Get command
                Command cmd = CorgiBot.getInstance().getCommandHandler().getCommand(invoke);

                if(cmd == null){
                    return;
                }

                Metrics.commandsReceived.labels(cmd.getClass().getSimpleName()).inc();

                // If Corgi does not own basic permission will do nothing
                if (!e.getGuild().getSelfMember().hasPermission(getBasicPerms())) {
                    return;
                }

                // Check bot owner
                if (cmd.isOwner() && !e.getAuthor().getId().equals("177516608778928129")){
                    return;
                }

                // Spam detection
                handleSpamDetection(e, guildWrapper, e.getChannel());

                // Blocking guild
                if (guildWrapper.isBlocked()) {
                    if (System.currentTimeMillis() > guildWrapper.getUnBlockTime() && guildWrapper.getUnBlockTime() != -1) {
                        guildWrapper.revokeBlock();
                    } else {
                        return; // Ignoring blocked guild
                    }
                }

                // Ignored channel
                if (guildWrapper.getIgnoredChannels().contains(e.getChannel()) && !cmd.getCommand().equalsIgnoreCase("ignore")) {
                    return;
                }

                // Info about sended command
                CorgiLogger.commandMessage("'" + cmd.getCommand() + " " + Arrays.toString(Arrays.copyOfRange(split, 1, split.length)) + "', (Guild: " + e.getGuild().getName() + ", Channel: " + (e.getChannel().getName()) + "), Sender: " + e.getAuthor());

                // Check bot permissions if are required
                if (!e.getGuild().getSelfMember().hasPermission(cmd.botPermission())) {
                    StringBuilder sb = new StringBuilder();
                    Arrays.stream(cmd.botPermission()).forEach(c -> sb.append(c.name()).append(", "));
                    MessageUtils.sendErrorMessage("Chyba práv", "Akci nelze provést, jelikož nemám dostatečná práva!\nChybí mi: `" + sb.toString().substring(0, sb.length() - 2) + "`", e.getChannel());
                    return;
                }

                // Check user permissions if are required
                if (!e.getMember().hasPermission(cmd.userPermission())) {
                    CorgiLogger.warnMessage("Prikaz zastaven - " + e.getAuthor().getName() + " nema dostatecna prava!");
                    return;
                }

                // Run command
                try {
                    Metrics.commandsExecuted.labels(cmd.getClass().getSimpleName()).inc();
                    cmd.onCommand(e.getChannel(), e.getMessage(), Arrays.copyOfRange(split, 1, split.length), e.getMember(), w, guildWrapper);
                } catch (Exception ex) {
                    MessageUtils.sendAutoDeletedMessage("Interní chyba při provádění příkazu!", 10000, e.getChannel());
                    ex.printStackTrace();
                }

                // Delete message after
                if (cmd.deleteMessage()) {
                    delete(e.getMessage());
                }

                // Statictics
                CorgiBot.commands++;
            }
        } catch (StringIndexOutOfBoundsException ex) {
            // ¯\_(ツ)_/¯
        } catch (ErrorResponseException ex2) {
            if (ex2.getErrorCode() == 50007) {
                e.getChannel().sendMessage(EmoteList.WARNING + " | " + e.getAuthor().getAsMention() + " promiň, ale nemohu ti poslat zprávu. Máš to blokované!").queue();
            } else {
                ex2.printStackTrace();
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
            CorgiLogger.debugMessage(String.format("---- DISCONNECT [SERVER] CODE: [%d] %s%n", event.getServiceCloseFrame()
                    .getCloseCode(), event
                    .getCloseCode()));
        else
            CorgiLogger.debugMessage(String.format("---- DISCONNECT [CLIENT] CODE: [%d] %s%n", event.getClientCloseFrame()
                    .getCloseCode(), event
                    .getClientCloseFrame().getCloseReason()));
    }

    private void delete(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    private void handleSpamDetection(GuildMessageReceivedEvent event, GuildWrapper guild, TextChannel ch) {
        if (spamMap.containsKey(event.getGuild().getId())) {
            int messages = spamMap.get(event.getGuild().getId());
            double allowed = Math.floor(Math.sqrt(getGuildUserCount(event.getGuild()) / 2.5));
            allowed = allowed == 0 ? 1 : allowed;
            if (messages > allowed) {
                if (!guild.isBlocked()) {
                    MessageUtils.sendErrorMessage("**Detekuji SPAM!** Od teď ignoruji na tomto serveru příkazy po dobu 1 minuty!", ch);
                    guild.setBlocked(true).setBlockReason("Spam příkazů").setUnBlockTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
                }
            } else {
                spamMap.put(event.getGuild().getId(), messages + 1);
            }
        } else {
            spamMap.put(event.getGuild().getId(), 1);
        }
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

    public void clearSpamMap() {
        spamMap.clear();
    }

    private Permission[] getBasicPerms() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_WRITE};
    }
}