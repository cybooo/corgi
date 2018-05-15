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
import org.json.JSONObject;

@SinceCorgi(version = "1.2.2")
public class Cat implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String url = "";
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("http://aws.random.cat/meow").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            url = (String) json.get("file");
        } catch (Exception e) {
            MessageUtils.sendErrorMessage("Nastala chyba při provádění příkazu. Zkus to znova zachvilku!", channel);
        }
        channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("Náhodný obrázek kočky:").setImage(url).build()).queue();
    }

    @Override
    public String getCommand() {
        return "cat";
    }

    @Override
    public String getDescription() {
        return "Náhodné obrázky koček!";
    }

    @Override
    public String getHelp() {
        return "%cat - Získání náhodného obrázku kočky";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"kocka", "rcat"};
    }
}
