package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.feeds.TwitterEventListener;
import cz.wake.corgibot.feeds.TwitterFeedObserver;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import twitter4j.TwitterException;
import twitter4j.User;

public class Feeds implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1){
            // HELP
        } else {
            if(args[0].equalsIgnoreCase("twitter")){
                if(args[1].equalsIgnoreCase("subscribe")){
                    String id = args[2];
                    long superId;
                    try {
                        superId = Long.valueOf(id);
                    } catch (Exception e){
                        MessageUtils.sendErrorMessage("ID neodpovídá Twitter formátu nebo se nejedná o ID uživatele!", channel);
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
                            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setDescription("Úspěšně přidaný Twitter účet **" + u.getName() + "**. Nyní sem budou chodit novinky z tohoto účtu.").build()).queue();
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
                }
            }
        }
    }

    @Override
    public String getCommand() {
        return "feeds";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"feed"};
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[0];
    }
}
