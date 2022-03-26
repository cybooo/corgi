package cz.wake.corgibot.commands.admin;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.pagination.old.Paginator;
import cz.wake.corgibot.utils.pagination.old.PaginatorBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CommandMarker
@SinceCorgi(version = "1.3.5")
public class IgnoreCommand extends ApplicationCommand {

    @JDASlashCommand(
            name = "ignore",
            description = "Command to toggle if Corgi should ignore all commands in the requested channel"
    )
    public void execute(GuildSlashEvent event) {
        if (!PermissionUtil.checkPermission(event.getMember(), Permission.MANAGE_CHANNEL)) {
            event.reply("You're not allowed to perform this command!").queue();
            return;
        }
        GuildWrapper gw = BotManager.getGuildWrapper(event.getGuild());
        event.getChannel().sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignoring channel: " + event.getChannel().getName())
                .setDescription("Disables the use of Corgi's commands in this channel.\nIf you want Corgi to stop ignoring, use `/ignore` again, and cancel it.\n\n" +
                        ":one: | " + formatTruth(event.getChannel(), gw) + " ignoring for this channel\n:two: | List all currently ignored channels").setFooter("If you want to cancel the action, do not react to it, it's gonna be be canceled within 30 seconds!", null).build()).queue((Message m) -> {

            m.addReaction(EmoteList.ONE).queue();
            m.addReaction(EmoteList.TWO).queue();

            CorgiBot.getInstance().getEventWaiter().waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 1
                return Objects.equals(e.getUser(), event.getMember().getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(EmoteList.ONE));
            }, (MessageReactionAddEvent ev) -> {
                m.delete().queue();
                ignoreChannel(ev.getChannel(), ev.getMember(), gw);
            }, 60, TimeUnit.SECONDS, () -> m.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription("Time's up!").build()).queue());

            CorgiBot.getInstance().getEventWaiter().waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 2
                return Objects.equals(e.getUser(), event.getMember().getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(EmoteList.TWO));
            }, (MessageReactionAddEvent ev) -> {
                m.delete().queue();
                shopIgnoredChannels(event.getChannel(), event.getMember(), CorgiBot.getInstance().getEventWaiter(), gw);
            }, 60, TimeUnit.SECONDS, null);
        });
    }

    private boolean getTruth(MessageChannel channel, GuildWrapper gw) {
        return gw.getIgnoredChannels().contains(channel);
    }

    private String formatTruth(MessageChannel channel, GuildWrapper gw) {
        boolean truth = getTruth(channel, gw);
        if (truth) {
            return "Disable";
        }
        return "Enable";
    }

    private void ignoreChannel(MessageChannel channel, Member member, GuildWrapper gw) {
        try {
            TextChannel ch = member.getGuild().getTextChannelById(channel.getId());
            if (gw.getIgnoredChannels().contains(ch)) {
                gw.updateIgnoredChannel(ch);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignoring channel: " + channel.getName())
                        .setDescription("🔔 | Corgi is now listening to all commands in this channel!")
                        .setFooter("You can enable ignoring again using `/ignore`", null).build()).queue();
                return;
            }
            gw.updateIgnoredChannel(ch);
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.ORANGE).setTitle("Ignoring channel: " + channel.getName())
                    .setDescription("🔕 | Corgi now ignores all commands in this channel!")
                    .setFooter("You can cancel ignoring using `/ignore`", null).build()).queue();
        } catch (Exception e) {
            MessageUtils.sendAutoDeletedMessage("Something went wrong! Try again later!", 35000L, channel);
        }

    }

    private void shopIgnoredChannels(MessageChannel channel, Member member, EventWaiter w, GuildWrapper gw) {
        List<MessageChannel> channels = gw.getIgnoredChannelsByMember(member);

        if (channels.isEmpty()) {
            MessageUtils.sendErrorMessage("No channels ignored!", channel);
            return;
        }

        PaginatorBuilder pBuilder = new PaginatorBuilder().setColumns(1)
                .setItemsPerPage(10)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException e) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(w)
                .setTimeout(1, TimeUnit.MINUTES);

        for (MessageChannel m : channels) {
            pBuilder.addItems(m.getName());
        }

        Paginator p = pBuilder.setColor(Constants.BLUE).setText("Ignored channels:").build();
        p.paginate(channel, 1);

    }

}
