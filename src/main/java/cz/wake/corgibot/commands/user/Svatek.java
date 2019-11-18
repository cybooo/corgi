package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.Arrays;

@SinceCorgi(version = "1.3.0")
public class Svatek implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {

            String czechName, slovakName;

            // API
            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("https://api.abalin.net/get/today").build();
            try {
                Response response = caller.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                JSONObject name = json.getJSONObject("data");

                czechName = (String) name.get("name_cz");
                slovakName = (String) name.get("name_sk");

                channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Kdo má dnes svátek?")
                        .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

            } catch (Exception e) {
                MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                e.getStackTrace();
            }
        } else {
            if(args[0].equalsIgnoreCase("zitra") || args[0].equalsIgnoreCase("zítra")){
                String czechName, slovakName;
                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/get/tomorrow").build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    czechName = (String) name.get("name_cz");
                    slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Kdo má zítra svátek?")
                            .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                    EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                    e.getStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("vcera") || args[0].equalsIgnoreCase("včera")){
                String czechName, slovakName;
                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/get/yesterday").build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    czechName = (String) name.get("name_cz");
                    slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Kdo měl včera svátek?")
                            .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                    EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                    e.getStackTrace();
                }
            } else {
                String date = args[0];
                String[] dateArray;
                if (date.matches("(0?[1-9]|[12]\\d|30|31).(0?[1-9]|1[0-2]).")) {
                    dateArray = date.split("\\.");
                    CorgiLogger.debugMessage(Arrays.toString(dateArray));
                } else {
                    MessageUtils.sendErrorMessage("Neplatně zadaný formát data. Zkus `1.12.`!", channel);
                    return;
                }

                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/get/namedays?day=" + dateArray[0] + "&month=" + dateArray[1]).build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    String czechName = (String) name.get("name_cz");
                    String slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle("Dne " + dateArray[0] + "." + dateArray[1] + ". má svátek:")
                            .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                    EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("Chyba v API nebo špatně zadaný datum!", channel);
                    e.getStackTrace();
                }
            }

        }
    }

    @Override
    public String getCommand() {
        return "svatek";
    }

    @Override
    public String getDescription() {
        return "Zjisti, kdo má dneska svátek v Česku a na Slovensku.";
    }

    @Override
    public String getHelp() {
        return "`%svatek` - Zobrazí jména, kteří mají dnes svátek\n" +
                "`%svatek zitra` - Zobrazí kdo má zítra svátek\n" +
                "`%svatek vcera` - Zobrazí kdo měl včera svátek\n" +
                "`%svatek [den].[mesic].` - Svátky pro konkrétní dny";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }
}
