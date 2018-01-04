package cz.wake.corgibot.managers;

public class CorgiUser {

    private String id;
    private int level;
    private int totalExp;
    private int toRankUp;
    private int pizzaStats;

    public CorgiUser(String id, int level, int toRankUp, int totalExp, int pizzaStats) {
        this.id = id;
        this.level = level;
        this.totalExp = totalExp;
        this.toRankUp = toRankUp;
        this.pizzaStats = pizzaStats;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int getPizzaStats() {
        return pizzaStats;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

    public void setPizzaStats(int pizzaStats) {
        this.pizzaStats = pizzaStats;
    }

    public int getToRankUp() {
        return toRankUp;
    }

    public void setToRankUp(int toRankUp) {
        this.toRankUp = toRankUp;
    }
}
