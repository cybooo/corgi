package cz.wake.corgibot.objects;

public class ChangeLog {

    private Long date;
    private String news;
    private String fixes;
    private String warning;

    public ChangeLog(Long date, String news, String fixes, String warning) {
        this.date = date;
        this.news = news;
        this.fixes = fixes;
        this.warning = warning;
    }

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
