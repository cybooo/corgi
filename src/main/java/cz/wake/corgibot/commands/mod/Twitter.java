package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.feeds.TwitterEventListener;
import cz.wake.corgibot.feeds.TwitterFeedObserver;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
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

public class Twitter implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Color.CYAN).setTitle("Twitter feeds")
                .setDescription("Twitter Feeds allow you to receive news from the Twitter account via Corgi. \n" +
                        "So if Corgi follows someone and he writes a tweet, Corgi will send the Tweet to a Discord channel.\n\n")
                .addField("Commands","**%twitter sub [ID]** - Follows tweets in a channel\n**%twitter list** - Shows the list of followed twitter accounts\n**%twitter unsub [ID]** - Stops following a twitter account".replace("%", gw.getPrefix()), false)
                .addField("Where to get a Account ID?", "http://gettwitterid.com/", false).build()).queue();
        } else {
            if(args[0].equalsIgnoreCase("subscribe") || args[0].equalsIgnoreCase("sub")){
                String id = args[1];
                long superId;
                try {
                    superId = Long.parseLong(id);
                } catch (Exception e){
                    channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription("ID neodpovídá Twitter formátu nebo se nejedná o ID uživatele!")
                            .setFooter("You can find a Account ID on: http://gettwitterid.com", null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if(TwitterEventListener.getObserver(u.getId(), message.getGuild()) != null) {
                        if(TwitterEventListener.getObserver(u.getId(), message.getGuild()).getDiscoChannel().equals(message.getChannel())) {
                            MessageUtils.sendErrorMessage("Twitter account **" + u.getScreenName() + " is already followed in this channel!", channel);
                            return;
                        }
                    }
                    try {
                        // Register
                        new TwitterFeedObserver(message.getChannel().getId(), u.getName(), true, false, false).subscribe(superId);
                        channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setDescription("Succesfully followed **" + u.getName() + "**. New tweets are gonna be sent here.").build()).queue();
                    } catch (Exception e){
                        e.printStackTrace(); //?
                    }
                } catch(TwitterException e) {
                    if(e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage("Twitter account **" + id + "** not found!", channel);
                    } else {
                        MessageUtils.sendErrorMessage("Something went wrong! Try again later..", channel);
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")){
                List<TwitterFeedObserver> thisGuilds = new ArrayList<>();
                for (List<TwitterFeedObserver> list : TwitterEventListener.getFeed().values()) {
                    for (TwitterFeedObserver observer : list) {
                        if (observer.getDiscoChannel() != null && observer.getDiscoChannel().getGuild().equals(message.getGuild())) {
                            thisGuilds.add(observer);
                        }
                    }
                }
                if (thisGuilds.isEmpty()) {
                    MessageUtils.sendErrorMessage("No twitter accounts follwed!", channel);
                } else {
                    thisGuilds.sort(Comparator.comparing(f -> f.getDiscoChannel().getName()));
                    PagedTableBuilder tb = new PagedTableBuilder();
                    tb.addColumn("Channel");
                    tb.addColumn("Twitter account");
                    for (TwitterFeedObserver observer : thisGuilds) {
                        List<String> row = new ArrayList<>();
                        row.add("#" + observer.getDiscoChannel().getName());
                        row.add(observer.getTwitterHandle());
                        tb.addRow(row);
                    }
                    PaginationUtil.sendPagedMessage(channel, tb.build(), 0, message.getAuthor(), "kek");
                }
            } else if (args[0].equalsIgnoreCase("unsubscribe") || args[0].equalsIgnoreCase("unsub")){
                String id = args[1];
                long superId;
                try {
                    superId = Long.parseLong(id);
                } catch (Exception e){
                    channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription("The ID does not match the Twitter format or is not a user ID!")
                        .setFooter("You can find a Account ID on: http://gettwitterid.com", null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if(TwitterEventListener.removeTwitterFeed(u.getId(), message.getGuild())) {
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | **" + u.getName() + "** has been unfollowed!!").build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Twitter account not followed, or not found.", channel);
                    }
                } catch(TwitterException e) {
                    if(e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage("Twitter account **" + superId + "** not found!", channel);
                    } else {
                        MessageUtils.sendErrorMessage("Something went wrong! Try again later..", channel);
                    }
                }
            }
        }
    }

    @Override
    public String getCommand() {
        return "twitter";
    }

    @Override
    public String getDescription() {
        return "Follow twitter accounts in a specified channel.";
    }

    @Override
    public String getHelp() {
        return "**%twitter sub [ID]** - Begins following a twitter account\n**%twitter list** - Shows a list of followed twitter accounts\n**%twitter unsub [ID]** - Unfollows a account\n\n You can get Account IDs on: http://gettwitterid.com/";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }
}
