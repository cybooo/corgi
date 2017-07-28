package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MainListener extends ListenerAdapter {

    private EventWaiter w;

    public MainListener(EventWaiter w){
        this.w = w;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getAuthor().isBot()) {
            return;
        }

        if (e.getMessage().getRawContent().startsWith(String.valueOf(CorgiBot.PREFIX)) //TODO: MySQL prefix
                && !e.getAuthor().isBot()) {
            String message = e.getMessage().getRawContent();
            String command = message.substring(1);
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, message.indexOf(" ") - 1);

                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            for (Command cmd : CorgiBot.getInstance().getCommandHandler().getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command)) {
                    String[] finalArgs = args;
                    if(cmd.getUse() == CommandUse.GUILD && e.isFromType(ChannelType.TEXT)){
                        //Handle guild chat
                        if(cmd.onlyCM() && !e.getGuild().getId().equalsIgnoreCase("")){
                            return;
                        }
                        if(cmd.getRank() == Rank.BOT_OWNER){
                            if (!isCreator(e.getMessage().getAuthor())) {
                                return;
                            }
                        } else if(cmd.getRank() == Rank.GUILD_OWNER){
                            //TODO: Check guild owner
                        } else if (cmd.getRank() == Rank.MODERATOR){
                            //TODO: Check moderator group
                        } else if (cmd.getRank() == Rank.PREMIUM){
                            //TODO: Check premium group
                        }
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w);
                        } catch (Exception ex) {
                            MessageUtils.sendException("Chyba při provádění příkazu", ex, e.getChannel());
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    } else if (cmd.getUse() == CommandUse.PRIVATE && e.isFromType(ChannelType.PRIVATE)){
                        //Handle text channel
                        if(e.isFromType(ChannelType.TEXT)){
                            return; //Blokace z ALL
                        }
                        if(cmd.getRank() == Rank.BOT_OWNER){
                            if (!isCreator(e.getMessage().getAuthor())) {
                                return;
                            }
                        } else if(cmd.getRank() == Rank.GUILD_OWNER){
                            //TODO: Check guild owner
                        }
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w);
                        } catch (Exception ex) {
                            MessageUtils.sendException("Chyba při provádění příkazu", ex, e.getChannel());
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    } else if (cmd.getUse() == CommandUse.ALL && (e.isFromType(ChannelType.PRIVATE) || e.isFromType(ChannelType.TEXT))) {
                        //Handle all others
                        if(e.isFromType(ChannelType.TEXT)){
                            if(cmd.onlyCM() && !e.getGuild().getId().equalsIgnoreCase("")){
                                return;
                            }
                        }
                        if(cmd.getRank() == Rank.BOT_OWNER){
                            if (!isCreator(e.getMessage().getAuthor())) {
                                return;
                            }
                        } else if(cmd.getRank() == Rank.GUILD_OWNER){
                            //TODO: Check guild owner
                        } else if (cmd.getRank() == Rank.MODERATOR){
                            //TODO: Check moderator group
                        } else if (cmd.getRank() == Rank.PREMIUM){
                            //TODO: Check premium group
                        }
                        try {
                            cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember(), w);
                        } catch (Exception ex) {
                            MessageUtils.sendException("Chyba při provádění příkazu", ex, e.getChannel());
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        CorgiBot.getInstance().getSql().onDisable();
    }

    public boolean isCreator(User user) {
        return user.getId().equals("177516608778928129"); //Wake ID
    }

    private void delete(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    // Wake Secret :O
    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent e) {
        User u = e.getUser();
        if (isCreator(u)) {
            if (e.getPreviousOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            } else if (e.getPreviousOnlineStatus() == OnlineStatus.ONLINE) {
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            } else {
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            }
        }

    }
}