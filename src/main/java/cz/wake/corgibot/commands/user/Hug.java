package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
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

public class Hug implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://some-random-api.ml/animu/hug").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            //JSONArray jsonArray = json.getJSONArray("data");
            //JSONObject jsonObject = jsonArray.getJSONObject(0);
            url = json.getString("link");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.api-failed"), channel);
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setTitle(EmoteList.COMET + " | " +  I18n.getLoc(gw, "commands.hug.title")).setImage(url).build()).queue();
    }

    @Override
    public String getCommand() {
        return "hug";
    }

    @Override
    public String getDescription() {
        return "Náhodný obrázek obejmutí!";
    }

    @Override
    public String getHelp() {
        return "%hug - Vygenerování náhodného obrázku";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }
}
