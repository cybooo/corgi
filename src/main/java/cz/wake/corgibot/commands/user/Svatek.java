package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.ValuesUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

@SinceCorgi(version = "1.3.0")
public class Svatek implements Command {

    //TODO: Zjednodusit API calls

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

                channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Kdo má dnes svátek?")
                        .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

            } catch (Exception e) {
                MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                e.getStackTrace();
            }
        } else {
            if(args[0].equalsIgnoreCase("zitra")){
                String czechName, slovakName;
                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/get/tomorrow").build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    czechName = (String) name.get("name_cz");
                    slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Kdo má zítra svátek?")
                            .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                    EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                    e.getStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("vcera")){
                String czechName, slovakName;
                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/get/yesterday").build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    czechName = (String) name.get("name_cz");
                    slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Kdo měl včera svátek?")
                            .setDescription(EmoteList.CZECH_FLAG + " " + czechName + "\n" +
                                    EmoteList.SLOVAK_FLAG + " " + slovakName).build()).queue();

                } catch (Exception e) {
                    MessageUtils.sendErrorMessage("Zřejmě chyba v API! Zkus to zachvilku :(", channel);
                    e.getStackTrace();
                }
            } else {
                String day = args[0];
                String month = args[1];

                if (!(ValuesUtil.isInt(day) || (ValuesUtil.isInt(month)))) {
                    MessageUtils.sendErrorMessage("Zadal jsi špatně číslo! Zkus to znova...", channel);
                }

                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.abalin.net/namedays?day=" + day + "&month=" + month).build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject name = json.getJSONObject("data");

                    String czechName = (String) name.get("name_cz");
                    String slovakName = (String) name.get("name_sk");

                    channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Dne " + day + "." + month + ". má svátek:")
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
                "`%svatek [den] [mesic]` - Svátky pro konkrétní dny";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }
}
