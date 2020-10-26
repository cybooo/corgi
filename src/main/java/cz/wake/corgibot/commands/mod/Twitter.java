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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Twitter implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Color.CYAN).setTitle("Nápověda k Twitter Feederu")
                .setDescription("Twitter Feeds umožňují dostávat novinky z Twitter channelu přes Corgiho. Pokuď tedy Corgi na žádost někoho sleduje, a ten napíše tweet, Corgi zašle Tweet i na Discord.\n\n")
                .addField("Příkazy","**%twitter sub [ID]** - Zahájí odběr tweetů v channelu\n**%twitter list** - Zobrazí seznam odebíraných Twitter účtů\n**%twitter unsub [ID]** - Zruší odběr zvolenému Twitter účtu".replace("%", gw.getPrefix()), false)
                .addField("Kde získat ID účtu", "http://gettwitterid.com/", false).build()).queue();
        } else {
            if(args[0].equalsIgnoreCase("subscribe") || args[0].equalsIgnoreCase("sub")){
                String id = args[1];
                long superId;
                try {
                    superId = Long.valueOf(id);
                } catch (Exception e){
                    channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription("ID neodpovídá Twitter formátu nebo se nejedná o ID uživatele!")
                            .setFooter("ID Twitter účtu lze zjistit na: http://gettwitterid.com", null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if(TwitterEventListener.getObserver(u.getId(), message.getGuild()) != null) {
                        if(TwitterEventListener.getObserver(u.getId(), message.getGuild()).getDiscoChannel().equals(message.getChannel())) {
                            MessageUtils.sendErrorMessage("Twitter účet **" + u.getScreenName() + " je již registrován v tomto channelu!", channel);
                            return;
                        }
                    }
                    try {
                        // Register
                        new TwitterFeedObserver(message.getChannel().getId(), u.getName(), true, false, false).subscribe(superId);
                        channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setDescription("Úspěšně přidaný Twitter účet **" + u.getName() + "**. Nyní sem budou chodit novinky z tohoto účtu.").build()).queue();
                    } catch (Exception e){
                        e.printStackTrace(); //?
                    }
                } catch(TwitterException e) {
                    if(e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage("Twitter účet **" + id + "** neexistuje!", channel);
                    } else {
                        MessageUtils.sendErrorMessage("Nastala chyba při requestu API! Zkus to zachvilku...", channel);
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
                    MessageUtils.sendErrorMessage("Na tomto serveru nejsou nastavené žádné Twitter Feeds!", channel);
                } else {
                    Collections.sort(thisGuilds, Comparator.comparing(f -> f.getDiscoChannel().getName()));
                    PagedTableBuilder tb = new PagedTableBuilder();
                    tb.addColumn("Channel");
                    tb.addColumn("Twitter účet");
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
                    superId = Long.valueOf(id);
                } catch (Exception e){
                    channel.sendMessage(MessageUtils.getEmbed(Constants.RED).setDescription("ID neodpovídá Twitter formátu nebo se nejedná o ID uživatele!")
                        .setFooter("ID Twitter účtu lze zjistit na: http://gettwitterid.com", null).build()).queue();
                    return;
                }
                try {
                    User u = TwitterEventListener.twitterClient.lookupUsers(superId).get(0);
                    if(TwitterEventListener.removeTwitterFeed(u.getId(), message.getGuild())) {
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.GREEN_OK + " | **" + u.getName() + "** byl úspěšně odebrán!").build()).queue();
                    } else {
                        MessageUtils.sendErrorMessage("Zadaný twitter účet není odebírán na tomto serveru, nebo neexistuje.", channel);
                    }
                } catch(TwitterException e) {
                    if(e.getErrorCode() == 17) {
                        MessageUtils.sendErrorMessage("Twitter účet **" + superId + "** neexistuje!", channel);
                    } else {
                        MessageUtils.sendErrorMessage("Nastala chyba při requestu API! Zkus to zachvilku...", channel);
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
        return "Twitter Feed pro vybrané kanály z Twittru.";
    }

    @Override
    public String getHelp() {
        return "**%twitter sub [ID]** - Zahájení odběru Twitter kanálu\n**%twitter list** - Zobrazí seznam odebíraných Twitter účtů\n**%twitter unsub [ID]** - Zruší odběr zvolenému Twitter účtu\n\n ID k účtům lze získat: http://gettwitterid.com/";
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
