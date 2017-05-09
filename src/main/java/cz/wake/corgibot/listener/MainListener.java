package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class MainListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        if(e.getAuthor().isBot()){
            return;
        }

        //TODO: Dodělat, typy příkazů podle channelu

        if(!e.isFromType(ChannelType.PRIVATE)){
            if (e.getMessage().getRawContent().startsWith(String.valueOf(CorgiBot.PREFIX))
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
                        if (cmd.getType() == CommandType.WAKE) {
                            if (!isCreator(e.getMessage().getAuthor())) {
                                return;
                            }
                        } else if (cmd.getType() == CommandType.ADMINISTRATIVE) {
                            if (!checkAdmin(e.getMessage())) {
                                return;
                            }
                        }
                        try {
                            cmd.onCommand(e.getAuthor(), e.getTextChannel(), e.getMessage(), finalArgs, e.getMember());
                        } catch (Exception ex) {
                            MessageUtils.sendException("Chyba při provádění příkazu", ex, e.getChannel());
                        }
                        if (cmd.deleteMessage()) {
                            delete(e.getMessage());
                        }
                    }
                }
            }
        } else {
            e.getChannel().sendMessage(MessageUtils.getEmbed(Color.RED).setDescription(":no_entry: | Neumím zatím odpovídat na soukromé zprávy.").build()).queue();
        }
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        CorgiBot.getInstance().getSql().onDisable();
    }

    public boolean isCreator(User user) {
        return user.getId().equals("177516608778928129");
    }

    public boolean checkAdmin(Message message) {
        for (Role role : message.getGuild().getRoles()) {
            if (role.getName().equalsIgnoreCase("STAFF") || role.getName().equalsIgnoreCase("ADMIN")
                    || role.getName().equalsIgnoreCase("HELPER") || role.getName().equalsIgnoreCase("MODERATOR")
                    || role.getName().equalsIgnoreCase("OWNER") || role.getName().equalsIgnoreCase("EVENTER")) {
                return true;
            }
        }
        return false;
    }

    private void delete(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }
    }

    // Wake Secret :O
    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent e){
        User u = e.getUser();
        if(isCreator(u)){
            if(e.getPreviousOnlineStatus() == OnlineStatus.DO_NOT_DISTURB){
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            } else if (e.getPreviousOnlineStatus() == OnlineStatus.ONLINE) {
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            } else {
                CorgiBot.getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            }
        }

    }
}