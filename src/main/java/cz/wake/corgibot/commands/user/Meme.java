package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.config.Config;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@SinceCorgi(version = "0.9")
public class Meme implements Command {

    private static final Map<String, String> map = new TreeMap<>();

    static {
        try {
            JSONObject memes = Unirest.get("https://api.imgflip.com/get_memes").asJson().getBody().getObject();

            JSONArray memeList = memes
                    .getJSONObject("data")
                    .getJSONArray("memes");

            for (int i = 0; i < memeList.length(); i++) {
                JSONObject jso = memeList.optJSONObject(i);
                map.put(jso.getString("name"), jso.getString("id"));
            }
        } catch (UnirestException e) {
            CorgiBot.LOGGER.error("Chyba při provádění příkazu emote!", e);
        }
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("**Použítí příkazu " + gw.getPrefix() + "meme**")
                    .setDescription("**" + gw.getPrefix() + "meme** - Zobrazí tuto nápovědu\n" +
                            "**" + gw.getPrefix() + "meme list [cislo]** - Zobrazí seznam všech dostupných obrázků\n" +
                            "**" + gw.getPrefix() + "meme [nazev] | [horni_radek] | [dolni_rade]** - Vygeneruje meme obrázek").build()).queue();
        } else if (args[0].equalsIgnoreCase("list")) {
            int page = 1;

            try {
                if (args[1] != null) {
                    page = Integer.valueOf(args[1]);
                    if (page <= 0) {
                        page = 1;
                    }
                }
            } catch (Exception ignore) {
            }

            Set<String> names = map.keySet();

            int pages;

            if (map.keySet().size() % 10 == 0) {
                pages = names.size() / 10;
            } else {
                pages = names.size() / 10 + 1;
            }

            if (page > pages) page = pages;

            int _page = page;

            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String g : names) {
                i++;
                if (i < 10 * _page + 1 && i > 10 * _page - 10) {
                    sb.append("**#").append(i).append("** [").append(g).append("]()").append('\n');
                }
            }

            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Meme list")
                    .setDescription(sb.toString()).setFooter("Strana [" + page + "/" + pages + "]", null).build()).queue();
        } else {
            try {
                String request = StringUtils.join(args, " ");
                String[] arguments = request.split("\\|");

                int ld = 999;
                String id = null;

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    int _d = StringUtils.getLevenshteinDistance(entry.getKey(), arguments[0].trim());

                    if (_d < ld) {
                        ld = _d;
                        id = entry.getValue();
                    }
                }

                Config config = CorgiBot.getConfig();

                JSONObject response = Unirest.get("https://api.imgflip.com/caption_image")
                        .queryString("template_id", id)
                        .queryString("username", "Corgi")
                        .queryString("password", config.getString("apis.imgflip"))
                        .queryString("text0", arguments[1].trim())
                        .queryString("text1", arguments[2].trim())
                        .asJson()
                        .getBody()
                        .getObject()
                        .getJSONObject("data");

                channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Vygenerované meme")
                        .setImage(response.optString("url")).build()).queue();
            } catch (Exception e) {
                MessageUtils.sendErrorMessage("Nesprávné použití příkazu! Správně **" + gw.getPrefix() + "meme [nazev] | [prvni_radek] | [druhy_radek]**", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "meme";
    }

    @Override
    public String getDescription() {
        return "Generování víc jak 100 MEME obrázků,\npodle vlastního textu.";
    }

    @Override
    public String getHelp() {
        return "%meme <nazev-predlohy> | <horni-text> | <dolni-text>\n" +
                "%meme list <strana> - Zobrazeni všech predloh";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }
}
