package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "leaveguild",
        help = "%leaveguild",
        description = "Command to force Corgi to leave the server, if confirmed. (Only for Administrators)",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.2.0")
public class LeaveGuild implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("\u26A0 Confirmation of leaving the server \u26A0")
                    .setDescription("**WARNING**: By confirming this action, corgi is gonna leave this server!\nAre you sure you want to perform the following action?").setFooter("You have 60 seconds to react.", null).build()).queue((Message m) -> {
                m.addReaction("\u2705").queue();
                m.addReaction("\u26D4").queue();
                message.delete().queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Potvrzení
                    return e.getUser().equals(member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals("\u2705"));
                }, (MessageReactionAddEvent ev) -> {
                    m.clearReactions().queue();
                    m.editMessage(MessageUtils.getEmbed(Constants.RED).setTitle("Action confirmed!").setDescription("Corgi is now leaving this server! :sob:").build()).queue();
                    m.getGuild().leave().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Zrušení
                    return e.getUser().equals(member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals("\u26D4"));
                }, (MessageReactionAddEvent ev) -> {
                    m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Action cancelled!").setDescription("Yaaay, Corgi is going to stay here! :hugging:").build()).queue();
                    m.clearReactions().queue();
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

}
