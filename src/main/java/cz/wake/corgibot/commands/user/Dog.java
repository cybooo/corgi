package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

@CommandInfo(
        name = "dog",
        aliases = {"rdog"},
        description = "Random dog images",
        help = "%dog - Generates random dog image",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.2.1")
public class Dog implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://dog.ceo/api/breeds/image/random").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            //JSONArray jsonArray = json.getJSONArray("data");
            //JSONObject jsonObject = jsonArray.getJSONObject(0);
            url = json.getString("message");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage("Something went wrong! Try again later..", channel);
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setTitle(EmoteList.DOG + " | " + "Random dog image:").setImage(url).build()).queue();
    }

}
