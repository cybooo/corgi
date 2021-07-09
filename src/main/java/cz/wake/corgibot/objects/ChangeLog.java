package cz.wake.corgibot.objects;

public record ChangeLog(Long date, String news, String fixes, String warning) {

    public Long getDate() {
        return date;
    }

    public String getNews() {
        return news;
    }

    public String getFixes() {
        return fixes;
    }

    public String getWarning() {
        return warning;
    }
}
