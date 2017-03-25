package cz.wake.corgibot.commands.admin;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.LinkedList;
import java.util.List;

public class Giveaway {

    int seconds;
    Message message;
    String item;

    public Giveaway(int time, Message message, String item) {
        seconds = time;
        this.message = message;
        this.item = item;
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (seconds > 5) {
                    message.editMessage(":tada:  **GIVEAWAY!**  :tada:\n" + (item != null ? "\u25AB*`" + item + "`*\u25AB\n" : "") + "Klikni na \uD83C\uDF89 ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).queue();
                    seconds -= 5;
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                }
                while (seconds > 0) {
                    message.editMessage(":tada: **G I V E A W A Y!** :tada:\nPOSLEDNÍ ŠANCE!!!\n" + (item != null ? "\u25AB*`" + item + "`*\u25AB\n" : "") + "Klikni na \uD83C\uDF89 ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).queue();
                    seconds--;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
                message.getChannel().getMessageById(message.getId()).complete().getReactions()
                        .stream().filter(mr -> mr.getEmote().getName().equals("\uD83C\uDF89"))
                        .findAny().ifPresent(mr -> {
                    List<User> users = new LinkedList<>(mr.getUsers().complete());
                    users.remove(message.getJDA().getSelfUser());
                    String id = users.get((int) (Math.random() * users.size())).getId();
                    message.editMessage(":tada: **GIVEAWAY SKONČIL!** :tada:\n" + (item != null ? "\u25AB*`" + item + "`*\u25AB\n" : "") + "\nVítěz: <@" + id + "> \uD83C\uDF89").queue();
                    message.getChannel().sendMessage("Gratulujeme <@" + id + ">! Vyhrál jsi" + (item == null ? "" : " " + item) + "!").queue();
                });
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
