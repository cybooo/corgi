package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class Giveaway {

    int seconds;
    Message message;
    String item;
    long now;

    public Giveaway(int time, Message message, String item) {
        seconds = time;
        this.message = message;
        this.item = item;
        now = System.currentTimeMillis() + (seconds * 1000);
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (seconds > 10) {
                    try {
                        message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY!**  :confetti_ball:", null).setDescription((item != null ? "\n**" + item + "**" : "\n") + "\nKlikni na 🎉 ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).setColor(Constants.GIVEAWAY_BLUE).setFooter("Konec ", null).setTimestamp(Instant.ofEpochMilli(now)).build()).queue();
                        seconds -= 5;
                        if(!message.getReactions().equals("\uD83C\uDF89")){
                            message.addReaction("\uD83C\uDF89").queue();
                        }
                        Thread.sleep(5000);
                    } catch (Exception ex){
                        CorgiBot.LOGGER.error("Giveaway", ex);
                        seconds = 0; //Force nastaveni aby bot nepocital tydny neco, co neexistuje.
                    }

                }
                while (seconds > 0) {
                    try {
                        message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY BRZO KONČÍ!**  :confetti_ball:", null).setDescription((item != null ? "\n**" + item + "**" : "\n") + "\nKlikni na 🎉 ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).setColor(Constants.RED).setFooter("Konec ", null).setTimestamp(Instant.ofEpochMilli(now)).build()).queue();
                        seconds--;
                        if(!message.getReactions().equals("\uD83C\uDF89")){
                            message.addReaction("\uD83C\uDF89").queue();
                        }
                        Thread.sleep(1000);
                    } catch (Exception ex){
                        CorgiBot.LOGGER.error("Giveaway", ex);
                    }

                }
                try {
                    message.getChannel().getMessageById(message.getId()).complete().getReactions()
                            .stream().filter(mr -> mr.getEmote().getName().equals("\uD83C\uDF89"))
                            .findAny().ifPresent(mr -> {
                        List<User> users = new LinkedList<>(mr.getUsers().complete());
                        users.remove(message.getJDA().getSelfUser());
                        String id = users.get((int) (Math.random() * users.size())).getId();
                        message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY SKONČIL!**  :confetti_ball:", null).setDescription((item != null ? "\n**" + item + "**" : "\n") + "\nVítěz: <@" + id + "> \uD83C\uDF89").setColor(Constants.GREEN).setFooter("Ukončeno ", null).setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis())).build()).queue();
                        message.getChannel().sendMessage("Gratulujeme <@" + id + ">! Vyhrál jsi" + (item == null ? "" : " " + item) + "!").queue();
                    });
                } catch (Exception ex) {
                    message.editMessage(new EmbedBuilder().setTitle(":fire:  **GIVEAWAY CHYBA!**  :fire:", null).setDescription("Vítěz nemohl být vyhodnocen, jelikož se nikdo nezúčastnil!").setColor(Constants.ORANGE).build()).queue();
                    message.clearReactions().queue();
                }
            }
        }.start();
    }

    public static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (years > 0) {
            builder.append("**").append(years).append("** let, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 365);
        }
        int weeks = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (weeks > 0) {
            builder.append("**").append(weeks).append("** týdnů, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 7);
        }
        int days = (int) (timeseconds / (60 * 60 * 24));
        if (days > 0) {
            builder.append("**").append(days).append("** dní, ");
            timeseconds = timeseconds % (60 * 60 * 24);
        }
        int hours = (int) (timeseconds / (60 * 60));
        if (hours > 0) {
            builder.append("**").append(hours).append("** hodin, ");
            timeseconds = timeseconds % (60 * 60);
        }
        int minutes = (int) (timeseconds / (60));
        if (minutes > 0) {
            builder.append("**").append(minutes).append("** minut, ");
            timeseconds = timeseconds % (60);
        }
        if (timeseconds > 0)
            builder.append("**").append(timeseconds).append("** vteřin");
        String str = builder.toString();
        if (str.endsWith(", "))
            str = str.substring(0, str.length() - 2);
        if (str.equals(""))
            str = "**RIP**";
        return str;
    }
}
