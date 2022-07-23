package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
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
        name = "dog",
        aliases = {"rdog"},
        description = "commands.dog.description",
        help = "commands.dog.help",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.2.1")
public class Dog implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://dog.ceo/api/breeds/image/random").build();
        try (Response response = caller.newCall(request).execute()) {
            JSONObject json = new JSONObject(response.body().string());
            url = json.getString("message");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.api-failed"), channel);
        }
        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(EmoteList.DOG + " | " + I18n.getLoc(gw, "commands.dog.title")).setImage(url).build()).queue();
    }

}
