package cz.wake.corgibot.commands.user;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@SinceCorgi(version = "1.3.1")
public class Weather implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu weather").setDescription(getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            String title, link, wind, citys;

            //Wind
            String speed;

            //Atmosphere
            String humidity, pressure, visibility;

            //Condition
            String temp, condes;
            int concode;

            citys = args[0];

            try {
                YahooWeatherService service = new YahooWeatherService();
                YahooWeatherService.LimitDeclaration limit = service.getForecastForLocation(citys, DegreeUnit.CELSIUS);

                List<Channel> list = limit.first(1);
                Channel city = list.get(0);

                //General Global
                title = city.getTitle();
                title = title.substring(16);
                link = city.getLink();

                //Wind
                speed = city.getWind().getSpeed() + " km/h"; //Wind speed
                wind = "Rychlost: " + speed;

                //Atmosphere
                humidity = city.getAtmosphere().getHumidity() + "%"; //Humidity in percents
                double pRound = city.getAtmosphere().getPressure();
                pressure = String.format("%.0f", pRound) + " psi"; //Pressure
                visibility = milesTokm(city.getAtmosphere().getVisibility()) + " km";

                //Condition
                condes = city.getItem().getCondition().getText();
                concode = city.getItem().getCondition().getCode();
                temp = city.getItem().getCondition().getTemp() + "°C";

                // Temperature in degree F
                double temper = (double) Math.round((city.getItem().getCondition().getTemp() * 1.8 + 32) * 100) / 100;
                String tempF = temper + "°F";

                String EmojiCon = setConditionEmoji(concode);

                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Počasí pro " + title, link, null);
                builder.setColor(Constants.BLUE);
                builder.setThumbnail(null);
                builder.setTimestamp(Instant.now());

                builder.addField(EmoteList.WINDY + " Vítr", wind, true);
                builder.addField(EmoteList.THERMOMETER + " Teplota", temp + "/" + tempF, true);
                builder.addField(EmoteList.DROPLET + " Vlhkost", humidity, true);
                builder.addField(EmoteList.COMPRESS + " Tlak", pressure, true);
                builder.addField(EmoteList.EYES + " Viditelnost", visibility, true);
                builder.addField(EmojiCon + " Souhrn", condes, true);

                channel.sendMessage(builder.build()).queue();
            } catch (JAXBException ex) {
                // Nic
            } catch (IOException ex2) {
                MessageUtils.sendErrorMessage("Chyba v API! Zkus to zachvilku...", channel);
            } catch (IndexOutOfBoundsException ex3) {
                MessageUtils.sendErrorMessage("Zadané město nebylo nalezeno!", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "Získej přehled o aktuálním počasí ve tvém městě.";
    }

    @Override
    public String getHelp() {
        return "%weather - Zobrazí nápovědu\n" +
                "%weather [město] - Zobrazí aktuální počasí pro zadané město.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pocasi", "wea"};
    }

    private static double milesTokm(double distanceInMiles) {
        return round(distanceInMiles * 1.60934, 3);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private String setConditionEmoji(int code) {
        String condition;
        switch (code) {
            //Sunny
            case 34:
            case 32:
                condition = EmoteList.SUNNY;
                break;

            //Cloudy
            case 26:
            case 27:
            case 28:
                condition = EmoteList.CLOUD;
                break;

            //Partly Cloudy
            case 29:
            case 30:
            case 44:
                condition = EmoteList.PARTLY_CLOUD;
                break;

            //Rain 1
            case 6:
            case 10:
            case 17:
            case 35:
            case 40:
                condition = EmoteList.CLOUD_SUN_RAIN;
                break;

            //Rain 2
            case 5:
            case 7:
            case 9:
            case 11:
            case 12:
            case 18:
                condition = EmoteList.CLOUD_RAIN;
                break;

            //Thunder Storm
            case 3:
            case 4:
            case 37:
            case 38:
            case 39:
            case 45:
            case 47:
                condition = EmoteList.THUNDER_CLOUD;
                break;

            //Snow
            case 13:
            case 14:
            case 15:
            case 16:
            case 46:
                condition = EmoteList.CLOUD_SNOW;
                break;

            //Tornado
            case 0:
            case 1:
            case 2:
                condition = EmoteList.TORNADO;
                break;

            //Dusty, WINDY, foggy, smoke
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
                condition = EmoteList.WINDY;
                break;

            case 21:
            case 25:
            case 41:
            case 42:
                condition = EmoteList.SNOWMAN;
                break;

            //Hot
            case 36:
                condition = EmoteList.HOT;
                break;

            default:
                condition = EmoteList.CLOUD;
                break;
        }
        return condition;
    }
}
