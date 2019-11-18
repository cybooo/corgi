package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

@SinceCorgi(version = "1.2.1")
public class Dog implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://api-to.get-a.life/dogimg").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            //JSONArray jsonArray = json.getJSONArray("data");
            //JSONObject jsonObject = jsonArray.getJSONObject(0);
            url = json.getString("link");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.api-failed"), channel);
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.LIGHT_BLUE).setTitle(EmoteList.DOG + " | " +  I18n.getLoc(gw, "commands.dog.title")).setImage(url).build()).queue();
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
