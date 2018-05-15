package cz.wake.corgibot.utils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.scheluder.CorgiTask;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Timer;
import java.util.stream.Collectors;

public class MessageUtils {

    /**
        Sends Exception to selected {@link MessageChannel}

        @param s Initial message
        @param e Throwable message {@link Throwable}
        @param channel Selected {@link MessageChannel} where Corgi will send Exception message
     */
    public static Message sendException(String s, Throwable e, MessageChannel channel) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String trace = sw.toString();
        pw.close();
        return sendErrorMessage(getEmbed().setDescription(s + "\n**Stack trace**: " + hastebin(trace)), channel);
    }

    public static String hastebin(String trace) {
        try {
            return "https://hastebin.com/" + Unirest.post("https://hastebin.com/documents")
                    .header("User-Agent", "Mozilla/5.0 FlareBot")
                    .header("Content-Type", "text/plain")
                    .body(trace)
                    .asJson()
                    .getBody()
                    .getObject().getString("key");
        } catch (UnirestException e) {
            CorgiBot.LOGGER.error("Chyba při posílání na HasteBin", e);
            return null;
        }
    }

    public static void editMessage(Message message, String content) {
        message.editMessage(content).queue();
    }

    public static Message sendFile(MessageChannel channel, String s, String fileContent, String filename) {
        ByteArrayInputStream stream = new ByteArrayInputStream(fileContent.getBytes());
        return channel.sendFile(stream, filename, new MessageBuilder().append(s).build()).complete();
    }

    public static String getTag(User user) {
        return user.getName() + '#' + user.getDiscriminator();
    }

    public static EmbedBuilder getEmbed(User user, Color c) {
        return getEmbed(c).setFooter("Požadavek od @" + getTag(user), user.getEffectiveAvatarUrl());
    }

    public static EmbedBuilder getEmbed(User user) {
        return getEmbed(ColorSelector.getRandomColor()).setFooter("Požadavek od @" + getTag(user), user.getEffectiveAvatarUrl());
    }

    public static EmbedBuilder getEmbed(Color c) {
        return new EmbedBuilder().setColor(c);
    }

    public static EmbedBuilder getEmbed(){ return new EmbedBuilder(); }

    public static String getAvatar(User user) {
        return user.getEffectiveAvatarUrl();
    }

    public static String getDefaultAvatar(User user) {
        return user.getDefaultAvatarUrl();
    }

    //TODO: Dodelat try
    public static Message sendErrorMessage(EmbedBuilder builder, MessageChannel channel) {
        return channel.sendMessage(builder.setColor(Constants.RED).build()).complete();
    }

    public static Message sendErrorMessage(String message, MessageChannel channel) {
        return channel.sendMessage(MessageUtils.getEmbed().setColor(Constants.RED).setDescription(message).build())
                .complete();
    }

    public static Message sendErrorMessage(String title, String message, MessageChannel channel) {
        return channel.sendMessage(MessageUtils.getEmbed().setColor(Constants.RED).setTitle(title).setDescription(message).build())
                .complete();
    }

    public static void sendAutoDeletedMessage(String message, long delay, MessageChannel channel) {
        sendAutoDeletedMessage(new MessageBuilder().setEmbed(MessageUtils.getEmbed().setColor(Constants.RED).setDescription(message).build()).build(), delay, channel);
    }

    public static void sendAutoDeletedMessage(String title, String message, long delay, MessageChannel channel) {
        sendAutoDeletedMessage(new MessageBuilder().setEmbed(MessageUtils.getEmbed().setTitle(title).setColor(Constants.RED).setDescription(message).build()).build(), delay, channel);
    }

    public static void sendAutoDeletedMessage(String message, long delay, MessageChannel channel, Color c) {
        sendAutoDeletedMessage(new MessageBuilder().setEmbed(MessageUtils.getEmbed().setColor(c).setDescription(message).build()).build(), delay, channel);
    }

    public static void sendAutoDeletedMessage(MessageEmbed embed, long delay, MessageChannel channel) {
        sendAutoDeletedMessage(new MessageBuilder().setEmbed(embed).build(), delay, channel);
    }

    public static void editMessage(EmbedBuilder embed, Message message) {
        editMessage(message.getContentRaw(), embed, message);
    }

    public static void editMessage(String s, EmbedBuilder embed, Message message) {
        if (message != null)
            message.editMessage(new MessageBuilder().append(s).setEmbed(embed.build()).build()).queue();
    }

    public static EmbedBuilder getEmbedError() {
        return new EmbedBuilder().setFooter("Chyba při provádění akce CorgiBot", CorgiBot.getJda().getSelfUser().getAvatarUrl());
    }

    private static void sendAutoDeletedMessage(Message message, long delay, MessageChannel channel) {
        Message msg = channel.sendMessage(message).complete();
        new CorgiTask("AutoDeleteTask") {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }.delay(delay);
    }

    public static String getMessage(String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            msg.append(arg).append(" ");
        }
        return msg.toString().trim();
    }

    public static String getMessage(String[] args, int min) {
        return Arrays.stream(args).skip(min).collect(Collectors.joining(" ")).trim();
    }

    public static String getMessage(String[] args, int min, int max) {
        StringBuilder message = new StringBuilder();
        for (int index = min; index < max; index++) {
            message.append(args[index]).append(" ");
        }
        return message.toString().trim();
    }

    public static void sendPrivateMessage(User user, String message) {
        try {
            user.openPrivateChannel().complete()
                    .sendMessage(message.substring(0, Math.min(message.length(), 1999))).queue();
            CorgiBot.LOGGER.info("Zasílání zprávy - " + user.getName() + "(" + user.getId() + ") -> " + message);
        } catch (ErrorResponseException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void sendPrivateMessage(MessageChannel channel, User user, String message) {
        try {
            user.openPrivateChannel().complete()
                    .sendMessage(message.substring(0, Math.min(message.length(), 1999))).queue();
        } catch (ErrorResponseException e) {
            channel.sendMessage(message).queue();
        }
    }

    public static void sendPrivateMessage(MessageChannel channel, User user, EmbedBuilder message) {
        try {
            user.openPrivateChannel().complete().sendMessage(message.build()).complete();
        } catch (ErrorResponseException e) {
            channel.sendMessage(message.build()).queue();
        }
    }

    public static String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(StringUtils.repeat("-", size + padding * 2));
            } else {
                ret.append(middle).append(StringUtils.repeat("-", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }

    public static String getFooter(String footer, int padding, int... sizes) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        int total = 0;
        for (int i = 0; i < sizes.length; i++) {
            int size = sizes[i];
            total += size + (i == sizes.length - 1 ? 0 : 1) + padding * 2;
        }
        sb.append(footer);
        sb.append(StringUtils.repeat(" ", total - footer.length()));
        sb.append("|\n");
        return sb.toString();
    }
}
