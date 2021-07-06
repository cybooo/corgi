package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.CorgiLogger;
import cz.wake.corgibot.utils.MessageUtils;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        name = "covid",
        description = "Display covid statistics of a country",
        help = "%covid <country> - Displays statistics of a country",
        category = CommandCategory.FUN
)
@SinceCorgi(version = "1.3.4")
public class Covid implements CommandBase {

    private final DecimalFormat df = new DecimalFormat("#,###,###,##0");

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        if (args.length == 0) {
            MessageUtils.sendErrorMessage("You need to provide a country!", channel);
        } else {
            List<String> stringList = Arrays.asList(args);
            String country = String.join(" ", stringList.subList(0, stringList.size()));

            WebUtils.ins.getText("https://coronavirus-19-api.herokuapp.com/countries/" + country).async((text) -> {
                if (text.equalsIgnoreCase("Country not found")) {
                    message.reply("Invalid country!").queue();
                    return;
                }

                JSONObject json = new JSONObject(text);

                final int cases = (int) json.get("cases");
                final int todayCases = (int) json.get("todayCases");
                final int casesPerOneMillion = (int) json.get("casesPerOneMillion");
                final int deaths = (int) json.get("deaths");
                final int todayDeaths = (int) json.get("todayDeaths");
                final int deathsPerOneMillion = (int) json.get("deathsPerOneMillion");
                final int active = json.get("active").toString().equals("null") ? 0 : (int) json.get("active");
                final int recovered = json.get("recovered").toString().equals("null") ? 0 : (int) json.get("recovered");
                final int critical = (int) json.get("critical");
                final int totalTests = (int) json.get("totalTests");
                final int testsPerOneMillion = (int) json.get("testsPerOneMillion");

                EmbedBuilder embed = EmbedUtils.getDefaultEmbed()
                        .setColor(Constants.BLUE)
                        .setTitle("Covid stats - " + country)
                        .addField("Cases", formatNum(cases), true)
                        .addField("Cases today", formatNum(todayCases), true)
                        .addField("Cases per million", formatNum(casesPerOneMillion), true)

                        .addField("Deaths", formatNum(deaths), true)
                        .addField("Deaths today", formatNum(todayDeaths), true)
                        .addField("Deaths per million", formatNum(deathsPerOneMillion), true)

                        .addField("Active cases", formatNum(active), true)
                        .addField("Recovered cases", formatNum(recovered), true)
                        .addField("Critical cases", formatNum(critical), true)

                        .addField("Total tests", formatNum(totalTests), true)
                        .addField("Tests oer million", formatNum(testsPerOneMillion), true)
                        .setFooter("Data accuracy is not guaranteed!");

                channel.sendMessageEmbeds(embed.build()).queue();

            }, error -> CorgiLogger.fatalMessage("Error while retrieving covid stats: " + error.getMessage()));
        }
    }

    public String formatNum(int num) {
        return df.format(num);
    }

}
