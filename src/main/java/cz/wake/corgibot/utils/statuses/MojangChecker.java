package cz.wake.corgibot.utils.statuses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MojangChecker extends TimerTask {

    private static final ConcurrentMap<MojangService, Integer> serviceStatus = new ConcurrentHashMap<>();
    private final JsonParser parser = new JsonParser();

    public static ConcurrentMap<MojangService, Integer> getServiceStatus() {
        return serviceStatus;
    }

    @Override
    public void run() {
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL("https://status.mojang.com/check").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            JsonArray array = (JsonArray) parser.parse(br.readLine());

            for (JsonElement object : array) {
                JsonObject obj = object.getAsJsonObject();
                String serviceUrl = obj.entrySet().iterator().next().getKey();
                MojangService service = MojangService.getService(serviceUrl);
                if (service != null) {
                    String status = obj.get(serviceUrl).getAsString();
                    if (status.equalsIgnoreCase("green")) {
                        if (serviceStatus.containsKey(service)) {
                            int time = serviceStatus.get(service);
                            serviceStatus.remove(service);
                            //alertChannels(service.getName() + " are back online! It was down for " + time + " minute" + (time == 1 ? "s" : "") + "!", Color.green);
                        }
                    } else if (status.equalsIgnoreCase("yellow")) {
                        if (!serviceStatus.containsKey(service))
                            serviceStatus.put(service, -1);
                    } else {
                        if (serviceStatus.containsKey(service)) {
                            serviceStatus.put(service, serviceStatus.get(service) + 1);
                        } else {
                            serviceStatus.put(service, 0);
                            //alertChannels(service.getName() + " have gone down!", Color.red);
                        }
                    }
                } else {
                    //MinecraftStatus.LOGGER.error("Unknown service! " + serviceUrl);
                }
            }
        } catch (Exception e) {
            // Ignore - mojang status page refuses connection too often, don't want my console full of errors.
//            e.printStackTrace();
        }
    }

    /*
    private void alertChannels(String message, Color color){
        for (IChannel channel : minecraftStatus.getStatusChannels()) {
            MessageUtils.sendMessage(new EmbedBuilder().withTitle("Status Update").withDesc(message).withColor(color), channel);
        }
    } */
}
