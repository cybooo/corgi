package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.feeds.TwitterEventListener;
import cz.wake.corgibot.feeds.TwitterFeedObserver;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import twitter4j.TwitterException;
import twitter4j.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandInfo(
        name = "twitter",
        description = "commands.twitter.description",
        help = "commands.twitter.help",
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL}
)
public class Twitter implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Color.CYAN).setTitle(I18n.getLoc(gw, "commands.twitter.embed-title"))
                    .setDescription(I18n.getLoc(gw, "commands.twitter.embed-description"))
                    .addField(I18n.getLoc(gw, "commands.twitter.embed-field-1-name"), I18n.getLoc(gw, "commands.twitter.embed-field-1-description").replace("%", gw.getPrefix()), false)
                    .addField(I18n.getLoc(gw, "commands.twitter.embed-field-2-name"), I18n.getLoc(gw, "commands.twitter.embed-field-2-description"), false).build()).queue();
        } else {
            if (args[0].equalsIgnoreCase("subscribe") || args[0].equalsIgnoreCase("sub")) {
                String id = args[1];
                long superId;
                try {
                    superId = Long.parseLong(id);
                } catch (Exception e) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(I18n.getLoc(gw, "commands.twitter.id-not-matching"))
                            .setFooter(I18n.getLoc(gw, "commands.twitter.find-id-on"), null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if (TwitterEventListener.getObserver(u.getId(), message.getGuild()) != null) {
                        if (TwitterEventListener.getObserver(u.getId(), message.getGuild()).getDiscoChannel().equals(message.getChannel())) {
                            MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.twitter.account-already-followed"), u.getScreenName()), channel);
                            return;
                        }
                    }
                    try {
                        // Register
                        new TwitterFeedObserver(message.getChannel().getId(), u.getName(), true, false, false).subscribe(superId);
                        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(String.format(I18n.getLoc(gw, "commands.twitter.succesfully-followed"), u.getName())).build()).queue();
                    } catch (Exception e) {
                        e.printStackTrace(); //?
                    }
                } catch (TwitterException e) {
                    if (e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.twitter.account-not-found"), id), channel);
                    } else {
                        MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.command-failed"), channel);
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                List<TwitterFeedObserver> thisGuilds = new ArrayList<>();
                for (List<TwitterFeedObserver> list : TwitterEventListener.getFeed().values()) {
                    for (TwitterFeedObserver observer : list) {
                        if (observer.getDiscoChannel() != null && observer.getDiscoChannel().getGuild().equals(message.getGuild())) {
                            thisGuilds.add(observer);
                        }
                    }
                }
                if (thisGuilds.isEmpty()) {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.twitter.no-accounts-followed"), channel);
                } else {
                    thisGuilds.sort(Comparator.comparing(f -> f.getDiscoChannel().getName()));
                    PagedTableBuilder tb = new PagedTableBuilder();
                    tb.addColumn(I18n.getLoc(gw, "commands.twitter.channel"));
                    tb.addColumn(I18n.getLoc(gw, "commands.twitter.twitter-account"));
                    for (TwitterFeedObserver observer : thisGuilds) {
                        List<String> row = new ArrayList<>();
                        row.add("#" + observer.getDiscoChannel().getName());
                        row.add(observer.getTwitterHandle());
                        tb.addRow(row);
                    }
                    PaginationUtil.sendPagedMessage(channel, tb.build(), 0, message.getAuthor(), "kek");
                }
            } else if (args[0].equalsIgnoreCase("unsubscribe") || args[0].equalsIgnoreCase("unsub")) {
                String id = args[1];
                long superId;
                try {
                    superId = Long.parseLong(id);
                } catch (Exception e) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.RED).setDescription(I18n.getLoc(gw, "commands.twitter.id-not-matching"))
                            .setFooter(I18n.getLoc(gw, "commands.twitter.find-id-on"), null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if (TwitterEventListener.removeTwitterFeed(u.getId(), message.getGuild())) {
                        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | " + String.format(I18n.getLoc(gw, "commands.twitter.unfollowed"), u.getName())).build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.twitter.not-followed-or-not-found"), channel);
                    }
                } catch (TwitterException e) {
                    if (e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.twitter.account-not-found"), superId), channel);
                    } else {
                        MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.command-failed"), channel);
                    }
                }
            }
        }
    }
}
