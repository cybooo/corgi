package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
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

@CommandInfo(
        name = "hug",
        description = "commands.hug.description",
        help = "commands.hug.help",
        category = CommandCategory.FUN
)
public class Hug implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://some-random-api.ml/animu/hug").build();
        try (Response response = caller.newCall(request).execute()) {
            JSONObject json = new JSONObject(response.body().string());
            url = json.getString("link");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.api-failed"), channel);
        }
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(EmoteList.COMET + " | " + I18n.getLoc(gw, "commands.hug.title")).setImage(url).build()).queue();
    }

}
