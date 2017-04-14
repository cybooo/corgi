package cz.wake.corgibot.listener;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MainListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
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
                        cmd.onCommand(e.getAuthor(), e.getChannel(), e.getMessage(), finalArgs, e.getMember());
                    } catch (Exception ex) {
                        MessageUtils.sendException("Chyba při provádění příkazu", ex, e.getChannel());
                    }
                    if (cmd.deleteMessage()) {
                        delete(e.getMessage());
                    }
                }
            }
        }
    }

    public boolean isCreator(User user) {
        return user.getId().equals("177516608778928129");
    }

    public boolean checkAdmin(Message message) {
        for (Role role : message.getGuild().getRoles()) {
            if (role.getName().equalsIgnoreCase("STAFF") || role.getName().equalsIgnoreCase("ADMIN")
                    || role.getName().equalsIgnoreCase("HELPER") || role.getName().equalsIgnoreCase("MODERATOR")
                    || role.getName().equalsIgnoreCase("OWNER")) {
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
}