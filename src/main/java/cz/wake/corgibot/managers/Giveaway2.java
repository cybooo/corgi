package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Giveaway2 {

    private int giveawayId;
    private long endTime;
    private String prize;
    private int maxWinners;
    private String emoji;
    private Color color;
    private Message message;
    private long seconds;
    private volatile boolean exit = false;

    public Giveaway2(Message message, long endTime, String prize, int maxWinners, String emoji, String color) {
        this.endTime = endTime;
        this.prize = prize;
        this.maxWinners = maxWinners;
        this.emoji = emoji != null ? emoji : "\uD83C\uDF89";
        try {
            this.color = color != null ? java.awt.Color.decode(color) : Constants.GIVEAWAY_BLUE;
        } catch (NumberFormatException e) {
            this.color = Constants.GIVEAWAY_BLUE;
        }
        this.message = message;
        this.seconds = (endTime - System.currentTimeMillis())/1000;
        this.giveawayId = 0;
    }

    public void start(){
        new Thread(() -> {
            try {
                if (exit){
                    return;
                }
                while (seconds > 10 && !exit) {
                    message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\nKlikni na " + emoji + " ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).setColor(color).setFooter("Výherci: " + maxWinners, null).setTimestamp(Instant.ofEpochMilli(endTime)).build()).queue(m -> {}, this::exceptionHandler);
                    seconds -= 5;
                    if(!message.getReactions().equals(emoji)){
                        message.addReaction(emoji).queue();
                    }
                    Thread.sleep(5000);
                }
                while (seconds > 0 && !exit) {
                    message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY BRZO SKONCI!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\nKlikni na " + emoji + " ke vstupu!\nZbývající čas: " + secondsToTime(seconds)).setColor(Constants.RED).setFooter("Výherci: " + maxWinners, null).setTimestamp(Instant.ofEpochMilli(endTime)).build()).queue(m -> {}, this::exceptionHandler);
                    seconds--;
                    if(!message.getReactions().equals(emoji)){
                        message.addReaction(emoji).queue();
                    }
                    Thread.sleep(1000);
                }
                try {
                    message.getChannel().getMessageById(message.getId()).complete().getReactions().stream().filter(mr -> mr.getReactionEmote().getName().equals(emoji)).findAny().ifPresent(mr -> {
                        List<User> users = new LinkedList<>(mr.getUsers().complete());
                        users.remove(message.getJDA().getSelfUser()); // Remove Corgi
                        List<String> winners = new ArrayList<>();
                        int failed = 0;
                        while (winners.size() < maxWinners){
                            String id = users.get((int) (Math.random() * users.size())).getId();
                            if(!winners.contains(id)){
                                winners.add(id);
                            } else {
                                if(failed >= 5){
                                    winners.add(null);
                                }
                                failed++;
                            }
                        }
                        StringBuilder finalWinners = new StringBuilder();
                        AtomicInteger c = new AtomicInteger();
                        winners.forEach(w -> {
                            c.getAndIncrement();
                            if(w == null){
                                finalWinners.append(c + ". `Nikdo`\n");
                            } else {
                                finalWinners.append(winners.size() > 1 ? c + ". " : "Vítěz ").append(message.getJDA().getUserById(w).getAsMention()).append("\n");
                            }
                        });
                        message.editMessage(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY SKONČIL!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\n" + finalWinners.toString()).setColor(Constants.GREEN).setFooter("Ukončeno ", null).setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis())).build()).queue(m -> {}, this::exceptionHandler);
                        CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(message.getGuild().getId(), message.getId());
                    });
                } catch (Exception ex){
                    message.editMessage(new EmbedBuilder().setTitle(":fire:  **GIVEAWAY CHYBA!**  :fire:", null).setDescription("Vítěz nemohl být vyhodnocen, jelikož se nikdo nezúčastnil!").setColor(Constants.ORANGE).build()).queue();
                    message.clearReactions().queue();
                    CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(message.getGuild().getId(), message.getId());
                }
            } catch (Exception ex) {
                Thread.currentThread().interrupt();
                CorgiLogger.fatalMessage("Corgi can not generate winners in giveaway in Guild (ID: " + message.getGuild().getId() + ").");
                CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(message.getGuild().getId(), message.getId());
                CorgiLogger.infoMessage("Corgi removed corrupted giveaway.");
            }
        }).start();
    }

    private static String secondsToTime(long timeseconds) {
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

    private void exceptionHandler(Throwable ex){
        if(ex instanceof ErrorResponseException){
            ErrorResponseException e = (ErrorResponseException)ex;
            switch(e.getErrorCode()){

                // Giveaway deleted.. Corgi do not have access to message
                case 10008: // message not found
                case 10003: // channel not found
                    CorgiLogger.fatalMessage("Giveaway has been deleted! Corgi will stop giveaway and remove data.");
                    requestExit();
                    Thread.currentThread().interrupt();
                    break;

                // Missing permissions for editing message
                case 50001: // missing access
                case 50013: // missing permissions
                    CorgiLogger.fatalMessage("Corgi can not edit giveaway. Thread stopped and removed.");
                    requestExit();
                    Thread.currentThread().interrupt();
                    break;

            }
        }
    }

    private void requestExit(){
        exit = true;
    }


}
