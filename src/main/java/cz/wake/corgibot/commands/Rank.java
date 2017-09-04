package cz.wake.corgibot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

public enum Rank {

    USER(1),
    PREMIUM(2),
    MODERATOR(3),
    ADMINISTRATOR(4),
    BOT_OWNER(5);

    private int rankWeight;

    Rank(int rankWeight) {
        this.rankWeight = rankWeight;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static Rank[] getTypes() {
        return new Rank[]{USER, PREMIUM, MODERATOR, ADMINISTRATOR, BOT_OWNER};
    }

    public String formattedName() {
        return toString();
    }

    public int getRankWeight() {
        return rankWeight;
    }

    public static Rank getPermLevelForUser(User user, Channel ch) {
        if (user.getId().equals("177516608778928129")) {
            return BOT_OWNER;
        }
        if (PermissionUtil.checkPermission(ch, ch.getGuild().getMember(user), Permission.ADMINISTRATOR)) {
            return ADMINISTRATOR;
        }
        if (PermissionUtil.checkPermission(ch, ch.getGuild().getMember(user), Permission.BAN_MEMBERS)) {
            return MODERATOR;
        }
        if (ch.getGuild() == null || !ch.getGuild().isMember(user)) {
            return USER;
        }
        return USER;
    }

    public boolean isAtLeast(Rank other) {
        return rankWeight >= other.rankWeight;
    }
}
