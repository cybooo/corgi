package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

@SinceCorgi(version = "1.2.1")
public class Dog implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.thedogapi.co.uk/v2/dog.php").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONArray jsonArray = json.getJSONArray("data");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            url = jsonObject.getString("url");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage("Nastala chyba při provádění příkazu. Zkus to znova zachvilku!", channel);
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Náhodný obrázek psa:").setImage(url).build()).queue();
    }

    @Override
    public String getCommand() {
        return "dog";
    }

    @Override
    public String getDescription() {
        return "Získání náhodného obrázku psa.";
    }

    @Override
    public String getHelp() {
        return "%dog - K získání obrázku psa.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pes", "rdog"};
    }
}
