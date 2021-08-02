package cz.wake.corgibot.objects;

import java.util.List;

public class Ticket {

    private final long guildId;
    private final long channelId;
    private final long ticketId;
    private final Long authorId;
    private final List<Long> addedUserIds;
    private final long createdTimestamp;

    public Ticket(long guildId, long channelId, long ticketId, Long authorId, List<Long> addedUserIds, long createdTimestamp) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.ticketId = ticketId;
        this.authorId = authorId;
        this.addedUserIds = addedUserIds;
        this.createdTimestamp = createdTimestamp;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public List<Long> getAddedUserIds() {
        return addedUserIds;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }
}