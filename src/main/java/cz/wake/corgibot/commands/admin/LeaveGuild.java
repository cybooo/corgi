package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "leaveguild",
        help = "commands.leave.help",
        description = "commands.leave.description",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.2.0")
public class LeaveGuild implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.ORANGE).setTitle(I18n.getLoc(gw, "commands.leave.embed-title"))
                    .setDescription(I18n.getLoc(gw, "commands.leave.embed-description")).setFooter(I18n.getLoc(gw, "commands.leave.footer"), null).build()).queue((Message m) -> {
                m.addReaction(Emoji.fromUnicode("✅")).queue();
                m.addReaction(Emoji.fromUnicode("⛔")).queue();
                message.delete().queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals("✅")), (MessageReactionAddEvent ev) -> {
                    m.clearReactions().queue();
                    m.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setTitle(I18n.getLoc(gw, "commands.leave.confirmed-title")).setDescription(I18n.getLoc(gw, "commands.leave.confirmed-description")).build()).queue();
                    m.getGuild().leave().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals("⛔")), (MessageReactionAddEvent ev) -> {
                    m.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setTitle(I18n.getLoc(gw, "commands.leave.cancelled-title")).setDescription(I18n.getLoc(gw, "commands.leave.cancelled-description")).build()).queue();
                    m.clearReactions().queue();
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

}
