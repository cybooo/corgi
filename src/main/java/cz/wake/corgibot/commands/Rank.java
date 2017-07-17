package cz.wake.corgibot.commands;

public enum Rank {

    USER(1),
    PREMIUM(2),
    MODERATOR(3),
    GUILD_OWNER(4),
    BOT_OWNER(5);

    private int rankWeight;

    Rank(int rankWeight) {
        this.rankWeight = rankWeight;
    }

    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static Rank[] getTypes() {
        return new Rank[]{USER, PREMIUM, MODERATOR, GUILD_OWNER, BOT_OWNER};
    }

    public String formattedName() {
        return toString();
    }


}
