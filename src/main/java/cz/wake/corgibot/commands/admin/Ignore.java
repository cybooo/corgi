package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SinceCorgi(version = "1.2.0")
public class Ignore implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignoring channel: " + channel.getName())
                    .setDescription("Disables the use of Corgi's commands in this channel.\nIf you want Corgi to stop ignoring, use `" + gw.getPrefix() + "ignore` again, and cancel it.\n\n" +
                            ":one: | " + formatTruth(channel, gw) + " ignoring for this channel\n:two: | List all currently ignored channels").setFooter("If you want to cancel the action, do not react to it, it's gonna be be canceled within 30 seconds!", null).build()).queue((Message m) -> {
                m.addReaction(EmoteList.ONE).queue();
                m.addReaction(EmoteList.TWO).queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 1
                    return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(EmoteList.ONE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    ignoreChannel(channel, member, gw.getPrefix(), gw);
                }, 60, TimeUnit.SECONDS, () -> m.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription("Time's up!").build()).queue());

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 2
                    return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals(EmoteList.TWO));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    shopIgnoredChannels(channel, member, w, gw);
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    @Override
    public String getCommand() {
        return "ignore";
    }

    @Override
    public String getDescription() {
        return "Command to toggle if Corgi should ignore all commands in the requested channel";
    }

    @Override
    public String getHelp() {
        return "%ignore - Toggle ignoring";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTRATOR;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
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

    private void ignoreChannel(MessageChannel channel, Member member, String prefix, GuildWrapper gw) {
        try {
            TextChannel ch = member.getGuild().getTextChannelById(channel.getId());
            if (gw.getIgnoredChannels().contains(ch)) {
                gw.updateIgnoredChannel(ch);
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignoring channel: " + channel.getName())
                        .setDescription("\uD83D\uDD14 | Corgi is now listening to all commands in this channel!")
                        .setFooter("You can enable ignoring again using `" + prefix + "ignore`", null).build()).queue();
                return;
            }
            gw.updateIgnoredChannel(ch);
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("Ignoring channel: " + channel.getName())
                    .setDescription("\uD83D\uDD15 | Corgi now ignores all commands in this channel!")
                    .setFooter("You can cancel ignoring using `" + prefix + "ignore`", null).build()).queue();
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

        Paginator p = pBuilder.setColor(Constants.DEFAULT_PURPLE).setText("Ignorred channels:").build();
        p.paginate(channel, 1);

    }
}
