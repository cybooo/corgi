package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.pagination.old.Paginator;
import cz.wake.corgibot.utils.pagination.old.PaginatorBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "ignore",
        help = "%ignore - Toggle ignoring",
        description = "Command to toggle if Corgi should ignore all commands in the requested channel",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "1.2.0")
public class Ignore implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle(String.format(I18n.getLoc(gw, "commands.ignore.embed-title"), channel.getName()))
                    .setDescription(String.format(I18n.getLoc(gw, "commands.ignore.embed-description"), gw.getPrefix(), formatTruth(channel, gw))).setFooter(I18n.getLoc(gw, "commands.ignore.footer"), null).build()).queue((Message m) -> {
                m.addReaction(Emoji.fromUnicode(EmoteList.ONE)).queue();
                m.addReaction(Emoji.fromUnicode(EmoteList.TWO)).queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 1
                    return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals(EmoteList.ONE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    ignoreChannel(channel, member, gw.getPrefix(), gw);
                }, 60, TimeUnit.SECONDS, () -> m.editMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(I18n.getLoc(gw, "commands.ignore.times-up")).build()).queue());

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 2
                    return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals(EmoteList.TWO));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    shopIgnoredChannels(channel, member, w, gw);
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }


    private boolean getTruth(MessageChannel channel, GuildWrapper gw) {
        return gw.getIgnoredChannels().contains(channel);
    }

    private String formatTruth(MessageChannel channel, GuildWrapper gw) {
        boolean truth = getTruth(channel, gw);
        if (truth) {
            return I18n.getLoc(gw, "commands.ignore.disables");
        }
        return I18n.getLoc(gw, "commands.ignore.enables");
    }

    private void ignoreChannel(MessageChannel channel, Member member, String prefix, GuildWrapper gw) {
        try {
            TextChannel ch = member.getGuild().getTextChannelById(channel.getId());
            if (gw.getIgnoredChannels().contains(ch)) {
                gw.updateIgnoredChannel(ch);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setTitle(String.format(I18n.getLoc(gw, "commands.ignore.title"), channel.getName()))
                        .setDescription(I18n.getLoc(gw, "commands.ignore.listening"))
                        .setFooter(String.format(I18n.getLoc(gw, "commands.ignore.enable-ignoring"), gw.getPrefix()), null).build()).queue();
                return;
            }
            gw.updateIgnoredChannel(ch);
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.ORANGE).setTitle(String.format(I18n.getLoc(gw, "commands.ignore.title"), channel.getName()))
                    .setDescription(I18n.getLoc(gw, "commands.ignore.ignoring"))
                    .setFooter(String.format(I18n.getLoc(gw, "commands.ignore.disable-ignoring"), gw.getPrefix()), null).build()).queue();
        } catch (Exception e) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "internal.error.command-failed"), 35000L, channel);
        }

    }

    private void shopIgnoredChannels(MessageChannel channel, Member member, EventWaiter w, GuildWrapper gw) {
        List<MessageChannel> channels = gw.getIgnoredChannelsByMember(member);

        if (channels.isEmpty()) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.ignore.no-channels-ignored"), channel);
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

        Paginator p = pBuilder.setColor(Constants.BLUE).setText(I18n.getLoc(gw, "commands.ignore.ignored-channels")).build();
        p.paginate(channel, 1);

    }
}
