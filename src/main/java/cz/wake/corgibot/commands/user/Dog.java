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
import org.json.JSONArray;
import org.json.JSONObject;

public class Dog implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        String url = new String();
        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.thedogapi.co.uk/v2/dog.php").build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONArray jsonArray = json.getJSONArray("data");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            url = jsonObject.getString("url");
        } catch (Exception e){
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
        return null;
    }

    @Override
    public String getHelp() {
        return null;
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
