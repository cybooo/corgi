package cz.wake.corgibot.feeds;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Guild;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.*;

public class TwitterEventListener {

    public static final long DELAY = 60;
    static final FilterQuery filter = new FilterQuery();
    private static final File serializedFile = new File("resources/feeds/Twitter.bin");
    // twitterClient is null if no API keys set
    public static Twitter twitterClient;
    static Configuration config;
    static TwitterStream twitterStream;
    private static long lastChange;
    private static Map<Long, List<TwitterFeedObserver>> twitterFeed = new HashMap<Long, List<TwitterFeedObserver>>();

    public static void initTwitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        try {
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(CorgiBot.getConfig().getString("feeds.twitter.appkey"))
                    .setOAuthConsumerSecret(CorgiBot.getConfig().getString("feeds.twitter.appsecret"))
                    .setOAuthAccessToken(CorgiBot.getConfig().getString("feeds.twitter.tokenkey"))
                    .setOAuthAccessTokenSecret(CorgiBot.getConfig().getString("feeds.twitter.tokensecret"));
            config = cb.build();
            TwitterFactory tf = new TwitterFactory(config);
            twitterClient = tf.getInstance();
            if (!serializedFile.createNewFile()) {
                serializedFile.getParentFile().mkdirs();
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializedFile));
                twitterFeed = (Map<Long, List<TwitterFeedObserver>>) ois.readObject();
                ois.close();
                if (!twitterFeed.keySet().isEmpty())
                    update();
            } else {
                saveFeed();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a twitterId to the feed with given observer
     * If it does not already exist, trigger a queue and return seconds until addition
     *
     * @param twitterId TwitterID to add
     * @param observer  Observer to associate
     * @return 1-60 if queued, -1 if already exists
     */
    public static int addTwitterFeed(long twitterId, TwitterFeedObserver observer) {
        if (!twitterFeed.containsKey(twitterId)) {
            ArrayList<TwitterFeedObserver> toAdd = new ArrayList<TwitterFeedObserver>();
            toAdd.add(observer);
            twitterFeed.put(twitterId, toAdd);
            saveFeed();
            return queue(twitterId);
        } else {
            twitterFeed.get(twitterId).add(observer);
            saveFeed();
            return -1;
        }
    }

    /**
     * Remove based on two params
     *
     * @param twitterId Twitter ID
     * @param guild     Guild to remove from
     * @return True if removed, false if doesn't exist
     */
    public static boolean removeTwitterFeed(long twitterId, Guild guild) {
        TwitterFeedObserver observer;
        if ((observer = getObserver(twitterId, guild)) == null)
            return false;
        twitterFeed.get(twitterId).remove(observer);
        saveFeed();
        return true;
    }

    /**
     * Get observer for this twitterId and guild
     *
     * @param twitterId Twitter ID to search
     * @param guild     Guild
     * @return Observer if found, null if either twitterId doesn't exist or observer doesn't exist in twitterId
     */
    public static TwitterFeedObserver getObserver(long twitterId, Guild guild) {
        if (!twitterFeed.containsKey(twitterId)) {
            return null;
        }
        for (TwitterFeedObserver observer : twitterFeed.get(twitterId)) {
            if (observer.getDiscoChannel().getGuild().equals(guild))
                return observer;
        }
        return null;
    }

    //private static Set<Long> queuedAdd = new HashSet<Long>();

    /**
     * Update the filters with contents of queuedAdd + previous keys of twitterFeed
     */
    public static void update() {
        long[] list = new long[twitterFeed.keySet().size()];
        Iterator<Long> iter = twitterFeed.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            list[i] = iter.next();
            i++;
        }
        if (twitterStream == null) {
            createNewStatusListener(config);
        }
        filter.follow(list);
        twitterStream.filter(filter);
    }

    public static void saveFeed() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedFile));
            oos.writeObject(twitterFeed);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Queue to add a twitter ID to the filter
     *
     * @param twitterId Twitter ID to add
     * @return Seconds until this takes effect
     */
    public static int queue(long twitterId) {
        if (!updateQueued()) {
            lastChange = System.currentTimeMillis();
            MessageUtils.setTimeout(TwitterEventListener::update, (int) (DELAY * 1000), true);
            return (int) DELAY;
        }
        return (int) (DELAY - (sinceLastChange() / 1000));
    }

    public static Map<Long, List<TwitterFeedObserver>> getFeed() {
        return twitterFeed;
    }

    /**
     * Initialize the Twitter status stream
     *
     * @param config Predone configuration
     */
    static void createNewStatusListener(Configuration config) {
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if (twitterFeed.containsKey(status.getUser().getId())) {
                    if (twitterFeed.get(status.getUser().getId()).isEmpty()) {
                        twitterFeed.remove(status.getUser().getId());
                        saveFeed();
                        return;
                    }
                    Iterator<TwitterFeedObserver> iter = twitterFeed.get(status.getUser().getId()).iterator();
                    while (iter.hasNext()) {
                        TwitterFeedObserver observer = iter.next();
                        if (!observer.trigger(status)) {
                            iter.remove();
                            saveFeed();
                        }
                    }
                }
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
            }

            @Override
            public void onStallWarning(StallWarning arg0) {
            }
        };
        twitterStream = new TwitterStreamFactory(config).getInstance();
        twitterStream.addListener(listener);
    }

    private static boolean updateQueued() {
        return sinceLastChange() < (DELAY * 1000);
    }

    /**
     * How long ago was the last edit
     *
     * @return Difference in milliseconds
     */
    public static long sinceLastChange() {
        return System.currentTimeMillis() - lastChange;
    }

    public static Map<Long, List<TwitterFeedObserver>> getTwitterList() {
        return twitterFeed;
    }
}
