package cz.wake.corgibot.listener;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatListener extends ListenerAdapter {

    private EventWaiter w;
    private static Map<String, Integer> spamMap = new ConcurrentHashMap<>();

    public ChatListener(EventWaiter w) {
        this.w = w;
    }

    public ChatListener() {}

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        if (e.getAuthor().isBot() || e.getAuthor().isFake() || e.getAuthor() == null || e.getMessage() == null) {
            return;
        }

        if (BotManager.getListGuilds() == null) return;

        String prefix;
        GuildWrapper guildWrapper;

        if(!CorgiBot.isIsBeta()){
            // Custom Guild prefix from SQL
            guildWrapper = BotManager.getCustomGuild(e.getMember().getGuild().getId());
            prefix = guildWrapper.getPrefix();
        } else {
            // Fake guild
            prefix = Constants.PREFIX;
            guildWrapper = new GuildWrapper(e.getGuild().getId()).setPrefix(prefix, false);
        }

        try {
            if (e.getMessage().getContentRaw().substring(0, 2).contains(Constants.PREFIX) || e.getMessage().getContentRaw().startsWith(prefix)
                    || e.getMessage().getContentRaw().substring(0, prefix.length()).contains(prefix)) {
                String message = e.getMessage().getContentRaw();
                String command;
                if (e.getMessage().getContentRaw().substring(0, 2).contains(Constants.PREFIX)) {
                    command = message.substring(2);
                } else {
                    command = message.substring(prefix.length());
                }
                String[] args = new String[0];
                if (message.contains(" ")) {
                    command = command.substring(0, message.indexOf(" ") - prefix.length());
                    args = message.substring(message.indexOf(" ") + 1).split(" ");
                }
                for (ICommand cmd : CorgiBot.getInstance().getCommandHandler().getCommands()) {
                    if (cmd.getCommand().equalsIgnoreCase(command) || Arrays.asList(cmd.getAliases()).contains(command)) {

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

                        String[] finalArgs = args;
                        CorgiBot.LOGGER.info("Command - '" + cmd.getCommand() + " " + String.join(" ", finalArgs) + "', (Guild: " + e.getGuild().getName() + ", Channel: " + (e.getChannel().getName()) + "), Sender: " + e.getAuthor());
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
                                ex.printStackTrace();
                            }
                            if (cmd.deleteMessage()) {
                                delete(e.getMessage());
                            }
                        }
                        CorgiBot.commands++;
                    }
                }
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

    public static int getGuildUserCount(Guild guild) {
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
}