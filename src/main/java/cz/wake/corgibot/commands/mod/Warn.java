package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class Warn implements ICommand {
    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1){
            MessageUtils.sendErrorMessage("Musíš napsat důvod!", channel);
            return;
    }
        if (message.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš nejdříve někoho označit!", channel);
            return;
        }
        List<Member> warned = new ArrayList<>();
        warned = message.getMentionedMembers();
        String[] str = message.getContentRaw().split("\\|");
        String reason = str[0].replace(gw.getPrefix() + "warn", "");
        for (Member warn : warned) {
            User x = warn.getUser();
            x.openPrivateChannel().queue(msg -> msg.sendMessage(MessageUtils.getEmbed(Constants.RED)
                    .setTitle("Varování")
                    .setDescription("Ze serveru: " + member.getGuild().getName() + " | Důvod: " + reason)
                    .build()).queue());
        }
    }

    @Override
    public String getCommand() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Varování uživatele na serveru";
    }

    @Override
    public String getHelp() {
        return "%warn @reason | @user [@user]";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }
}
