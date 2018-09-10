package cz.wake.corgibot.commands;

import cz.wake.corgibot.managers.BotManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandEvent {

    private final MessageReceivedEvent event;
    private String[] args;
    private final CommandClient client;

    public CommandEvent(MessageReceivedEvent event, String[] args, CommandClient client) {
        this.event = event;
        this.args = args;
        this.client = client;
    }

    public String[] getArgs() {
        return args;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public CommandClient getClient() {
        return client;
    }

    void setArgs(String[] args) {
        this.args = args;
    }

    public boolean isBotOwner() {
        return event.getAuthor().getId().equals(client.getBotOwner());
    }

    public boolean isBotAdmin() {
        return isBotOwner() || client.getBotAdmins().contains(event.getAuthor().getId());
    }

    public boolean isBotMod() {
        return isBotOwner() || isBotAdmin() || client.getBotMods().contains(event.getAuthor().getId());
    }

    public ChannelType getChannelType() {
        return event.getChannelType();
    }

    public boolean isFromType(ChannelType type) {
        return type == event.getChannelType();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public String getPrefix() {
        return BotManager.getCustomGuild(getGuild().getId()).getPrefix();
    }

    public User getAuthor() {
        return event.getAuthor();
    }

    public Member getMember() {
        return event.getMember();
    }

    public SelfUser getSelfUser() {
        return event.getJDA().getSelfUser();
    }

    public Member getSelfMember() {
        return event.getGuild() == null ? null : event.getGuild().getSelfMember();
    }

    public JDA getJDA() {
        return event.getJDA();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public String getLocale() {
        return client.getLocale(event.getGuild().getId());
    }

    //TODO: Locales 180n

    /**
     * Use this over {@link Message#getMentionedUsers()}.
     *
     * @return List of mentioned users, removes self ping if used as a prefix.
     */
    public List<User> getMentionedUsers() {
        List<User> mentionedUsers = new ArrayList<>(event.getMessage().getMentionedUsers());
        if (event.getMessage().getContentRaw().startsWith(event.getGuild().getSelfMember().getAsMention()))
            mentionedUsers.remove(0);
        return mentionedUsers;
    }

    public String combineArgs() {
        return combineArgs(0, args.length);
    }

    public String combineArgs(int start) {
        return combineArgs(start, args.length);
    }

    public String combineArgs(int start, int end) {
        if (end > args.length)
            throw new IllegalArgumentException("End value specified is longer than the arguments provided.");
        return StringUtils.join(Arrays.copyOfRange(args, start, end), " ");
    }

    public boolean isDisabled() {
        return client.isUserDisabled(event.getAuthor().getId());
    }


}
