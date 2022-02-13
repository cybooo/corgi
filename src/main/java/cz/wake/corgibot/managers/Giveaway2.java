package cz.wake.corgibot.managers;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Giveaway2 {

    private final int giveawayId;
    private final long endTime;
    private final String prize;
    private final int maxWinners;
    private String emoji;
    private final Message message;
    private Color color;
    private long seconds;
    private volatile boolean exit = false;

    public Giveaway2(Message message, long endTime, String prize, int maxWinners, String emoji, String color) {
        this.endTime = endTime;
        this.prize = prize;
        this.maxWinners = maxWinners;
        this.emoji = emoji != null ? emoji : "🎉";
        try {
            this.color = color != null ? java.awt.Color.decode(color) : Constants.GIVEAWAY_BLUE;
        } catch (NumberFormatException e) {
            this.color = Constants.GIVEAWAY_BLUE;
        }
        this.message = message;
        this.seconds = (endTime - System.currentTimeMillis()) / 1000;
        this.giveawayId = 0;
    }

    private static String secondsToTime(long timeseconds) {
        StringBuilder builder = new StringBuilder();
        int years = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (years > 0) {
            builder.append("**").append(years).append("** years, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 365);
        }
        int weeks = (int) (timeseconds / (60 * 60 * 24 * 365));
        if (weeks > 0) {
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds = timeseconds % (60 * 60 * 24 * 7);
        }
        int days = (int) (timeseconds / (60 * 60 * 24));
        if (days > 0) {
            builder.append("**").append(days).append("** days, ");
            timeseconds = timeseconds % (60 * 60 * 24);
        }
        int hours = (int) (timeseconds / (60 * 60));
        if (hours > 0) {
            builder.append("**").append(hours).append("** hours, ");
            timeseconds = timeseconds % (60 * 60);
        }
        int minutes = (int) (timeseconds / (60));
        if (minutes > 0) {
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds = timeseconds % (60);
        }
        if (timeseconds > 0)
            builder.append("**").append(timeseconds).append("** seconds");
        String str = builder.toString();
        if (str.endsWith(", "))
            str = str.substring(0, str.length() - 2);
        if (str.equals(""))
            str = "**RIP**";
        return str;
    }

    public void start() {
        new Thread(() -> {
            try {
                if (exit) {
                    return;
                }
                while (seconds > 10 && !exit) {
                    message.editMessageEmbeds(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\nReact with" + emoji + " to join!\nRemaining time: " + secondsToTime(seconds)).setColor(color).setFooter("Winners: " + maxWinners, null).setTimestamp(Instant.ofEpochMilli(endTime)).build()).queue(m -> {
                    }, this::exceptionHandler);
                    seconds -= 5;
                    if (!message.getReactions().equals(emoji)) {
                        try {
                            message.addReaction(emoji).queue();
                        } catch (Exception e) {
                            emoji = "🎉";
                            message.addReaction(emoji).queue();
                            exceptionHandler(e);
                        }
                    }
                    Thread.sleep(60000);
                }
                while (seconds > 0 && !exit) {
                    message.editMessageEmbeds(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY IS ENDING SOON!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\nReact with " + emoji + " to join!\nRemaining time: " + secondsToTime(seconds)).setColor(Constants.RED).setFooter("Winners: " + maxWinners, null).setTimestamp(Instant.ofEpochMilli(endTime)).build()).queue(m -> {
                    }, this::exceptionHandler);
                    seconds--;
                    if (!message.getReactions().equals(emoji)) {
                        try {
                            message.addReaction(emoji).queue();
                        } catch (Exception e) {
                            emoji = "🎉";
                            message.addReaction(emoji).queue();
                            exceptionHandler(e);
                        }                 }
                    Thread.sleep(20000);
                }
                try {
                    message.getChannel().retrieveMessageById(message.getId()).complete().getReactions().stream().filter(mr -> mr.getReactionEmote().getName().equals(emoji)).findAny().ifPresent(mr -> {
                        List<User> users = new LinkedList<>(mr.retrieveUsers().complete());
                        users.remove(message.getJDA().getSelfUser()); // Remove Corgi
                        List<String> winners = new ArrayList<>();
                        int failed = 0;
                        while (winners.size() < maxWinners) {
                            String id = users.get((int) (Math.random() * users.size())).getId();
                            if (!winners.contains(id)) {
                                winners.add(id);
                            } else {
                                if (failed >= 5) {
                                    winners.add(null);
                                }
                                failed++;
                            }
                        }
                        StringBuilder finalWinners = new StringBuilder();
                        AtomicInteger c = new AtomicInteger();
                        winners.forEach(w -> {
                            c.getAndIncrement();
                            if (w == null) {
                                finalWinners.append(c).append(". `Noone`\n");
                            } else {
                                finalWinners.append(winners.size() > 1 ? c + ". " : "Winnner ").append(message.getJDA().getUserById(w).getAsMention()).append("\n");
                            }
                        });
                        message.editMessageEmbeds(new EmbedBuilder().setTitle(":confetti_ball:  **GIVEAWAY ENDED!**  :confetti_ball:", null).setDescription((prize != null ? "\n**" + prize + "**" : "\n") + "\n" + finalWinners).setColor(Constants.GREEN).setFooter("Ended ", null).setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis())).build()).queue(m -> {
                        }, this::exceptionHandler);

                        if (winners.size() > 1) {
                            StringBuilder finalList = new StringBuilder();
                            winners.forEach(winner -> {
                                if (winner != null) {
                                    finalList.append(message.getJDA().getUserById(winner).getAsMention()).append(", ");
                                }
                            });
                            message.getChannel().sendMessage("Congratulations " + StringUtils.removeEnd(finalList.toString(), ", ") + (prize != null ? "! You won **" + prize + "**" : "You won!")).queue();
                        } else {
                            winners.forEach(winner -> {
                                if (winner != null) {
                                    message.getChannel().sendMessage("Congratulations " + message.getJDA().getUserById(winner).getAsMention() + (prize != null ? "! You won **" + prize + "**" : "You won!")).queue();
                                }
                            });
                        }
                        CorgiBot.getInstance().getSql().deleteGiveawayFromSQL(message.getGuild().getId(), message.getId());
                    });
                } catch (Exception ex) {
                    message.editMessageEmbeds(new EmbedBuilder().setTitle(":fire:  **GIVEAWAY ERROR!**  :fire:", null).setDescription("No winner found, noone has joined the giveaway!").setColor(Constants.ORANGE).build()).queue();
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

    private void exceptionHandler(Throwable ex) {
        if (ex instanceof ErrorResponseException e) {
            switch (e.getErrorCode()) {

                // Giveaway deleted.. Corgi does not have access to message
                case 10008, 10003 -> { // 10008 = message not found | 10003 = channel not found
                    CorgiLogger.fatalMessage("Giveaway has been deleted! Corgi will stop giveaway and remove data.");
                    requestExit();
                    Thread.currentThread().interrupt();
                }

                case 50001, 50013 -> { // 50001 = missing access | 50013 = missing permissions
                    CorgiLogger.fatalMessage("Corgi can not edit giveaway. Thread stopped and removed.");
                    requestExit();
                    Thread.currentThread().interrupt();
                }
                case 50035 -> { // 50035 = Invalid Form Body (Wrong snowflake?)
                    CorgiLogger.fatalMessage("Emoji could not be added, switching to default.");
                }
                default -> {
                    CorgiLogger.fatalMessage("Something went wrong! (Giveaway2:exceptionHandler)");
                }
            }
        }
    }

    private void requestExit() {
        exit = true;
    }


}
