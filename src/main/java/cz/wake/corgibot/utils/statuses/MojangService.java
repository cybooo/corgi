package cz.wake.corgibot.utils.statuses;

public enum MojangService {

    MINECRAFT_NET("Minecraft.net", "minecraft.net"),
    SESSIONS("Mojang Sessions", "session.minecraft.net"),
    ACCOUNT("Mojang Accounts", "account.mojang.com"),
    AUTH("Mojang Authentication", "auth.mojang.com"),
    SKINS("Skins", "skins.minecraft.net"),
    AUTH_SERVER("Authentication Server", "authserver.mojang.com"),
    //SESSION_SERVER("Session Server", "sessionserver.mojang.com"),
    API("API", "api.mojang.com"),
    TEXTURES("Textures", "textures.minecraft.net");
    //MOJANG_COM("Mojang.com", "mojang.com");

    public static MojangService[] values = values();

    private final String name;
    private final String url;

    MojangService(String prettyName, String url) {
        this.name = prettyName;
        this.url = url;
    }

    @Override
    public String toString() {
        return name + " (" + url + ")";
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public static MojangService getService(String serviceUrl) {
        for (MojangService service : values) {
            if (service.getUrl().equalsIgnoreCase(serviceUrl)) {
                return service;
            }
        }
        return null;
    }

    public static MojangService getServiceByName(String serviceName) {
        for (MojangService service : values) {
            if (service.getName().equalsIgnoreCase(serviceName))
                return service;
        }
        return null;
    }
}
