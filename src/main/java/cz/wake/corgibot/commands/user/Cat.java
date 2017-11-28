package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class Cat implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        String url = new String();
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("http://random.cat/meow").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            url = (String) json.get("file");
        } catch (Exception e){
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
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
