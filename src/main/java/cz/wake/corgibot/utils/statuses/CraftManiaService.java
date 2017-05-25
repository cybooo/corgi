package cz.wake.corgibot.utils.statuses;

public enum CraftManiaService {

    //https://status.craftmania.cz/api/v1/components

    REDIS(2,"Redis","play.craftmania.cz"),
    BUNGEECORD(1,"Bungeecord","play.craftmania.cz"),
    MYSQL(5,"Hlavní MySQL","mysql.craftmania.cz"),
    WEB(3,"Web","https://craftmania.cz"),
    TEAMSPEAK(4,"TeamSpeak","ts.craftmania.cz"),
    GITLAB(7,"GitLab","git.waked.cz"),
    PROMETHEUS(12,"Prometheus","prometheus.craftmania.cz"),
    BUDDY(13,"Buddy","buddy.craftmania.cz");

    public static CraftManiaService[] values = values();

    private int id;
    private String name;
    private String url;

    CraftManiaService(int id, String name, String url){
        this.id = id;
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString(){
        return name + " (" + url + ")";
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getUrl(){
        return this.url;
    }

    public static CraftManiaService getService(int id){
        for(CraftManiaService s : values){
            if(s.id == id){
                return s;
            }
        }
        return null;
    }

}
