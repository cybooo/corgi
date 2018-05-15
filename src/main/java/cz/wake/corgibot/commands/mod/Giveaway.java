package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.managers.Giveaway2;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.FormatUtil;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.TimeUtils;
import cz.wake.corgibot.utils.pagination.PagedTableBuilder;
import cz.wake.corgibot.utils.pagination.PaginationUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SinceCorgi(version = "2.3.2")
public class Giveaway implements Command {

    private static final PeriodFormatter periodParser = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();


    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        /**
         * c!giveaway 1h30m | Výhra v loterii | 2 | :smile: | #fffff
         * c!giveaway list
         */

        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GIVEAWAY_BLUE).setTitle("Jak na Giveaway")
                    .setDescription("Stručný návod jak vytvářet různé Giveawaye podle sebe.")
                    .addField("Vytvoření", "`{%}giveaway 30m` - Základní Giveaway na 30 minut.\n`{%}giveaway 1h | FarCry 3` - Giveaway na 1h s výhrou FarCry 3\n`{%}giveaway 2h | Mafia 2 | 5` - Giveaway na 2h, s výhrou Mafia 2 pro 5 uživatelů.\n`{%}giveaway 1d3h | Overwatch | 1 | \uD83D\uDE04` - Giveaway s vlastním emoji (zatím fungují pouze základní)\n`{%}giveaway 4d | CS:GO | 3 | \uD83D\uDE04 | #fffff` - Giveaway s vlastní barvou".replace("{%}", gw.getPrefix()), true)
                    .addField("Seznam giveawayů", "Pokuď na serveru běží více Giveawayů, lze zobrazit základní informace pomocí následujícího příkazu: `{%}giveaway list`".replace("{%}", gw.getPrefix()), true)
                    .addField("Smazání", "Giveaway se smaže zcela jednoduše, stačí smazat zprávu, kterou Corgi vytvořil!".replace("{%}", gw.getPrefix()), true).setFooter("Corgi vše ukládá, v případě výpadku bude Giveaway pokračovat.", null).build()).queue();
        } else {
            if (args[0].equalsIgnoreCase("list")) {

                PagedTableBuilder pb = new PagedTableBuilder();
                pb.addColumn("ID");
                pb.addColumn("Výhra");
                pb.addColumn("Počet");
                pb.addColumn("Konec za");

                CorgiBot.getInstance().getSql().getAllGiveaways().forEach(g -> {
                    if (g.getGuildId().equals(message.getGuild().getId())) {
                        List<String> row = new ArrayList<>();
                        row.add(String.valueOf(g.getGiveawayId()));
                        row.add(g.getPrize());
                        row.add(String.valueOf(g.getMaxWinners()));
                        row.add(TimeUtils.toShortTime(g.getEndTime() - System.currentTimeMillis()));
                        pb.addRow(row);
                    }
                });

                PaginationUtil.sendPagedMessage(channel, pb.build(), 0, message.getAuthor(), "giveaway list");
            } else {
                // Format message
                String request = message.getContentRaw().replaceAll("\\s+\\|", "|").replaceAll("\\|\\s+", "|").replaceAll("\\|", "|").replace("giveaway ", "").replace("gw ", "").replace(gw.getPrefix(), "");
                String[] arguments = request.split("\\|");

                // Time
                String time = arguments[0].replaceAll("\\s+", "");

                // Prize
                String prize = null;
                if (arguments.length >= 2) {
                    prize = arguments[1];
                }

                // Winners
                String maxWinners;
                int winners = 1;
                if (arguments.length >= 3) {
                    maxWinners = arguments[2].replaceAll("\\s+", "");
                    if (FormatUtil.isStringInt(maxWinners)) {
                        winners = Integer.valueOf(maxWinners);
                    } else {
                        MessageUtils.sendErrorMessage("Špatně zadaný počet výherců! Zkus to znova...", channel);
                        return;
                    }
                }

                // Emoji
                String emoji = null;
                if (arguments.length >= 4) {
                    emoji = arguments[3].replaceAll("\\s+", "");
                }

                // Color
                String color = null;
                if (arguments.length >= 5) {
                    if (Pattern.compile("#?([A-Fa-f\\d]){6}").matcher(color).find()) {
                        color = arguments[4].replaceAll("\\s+", "");
                    } else {
                        MessageUtils.sendErrorMessage("Špatně zadaný formát barvy! Správný formát: #00000", channel);
                        return;
                    }
                }

                Period p = getTimeFromInput(time, channel);
                DateTime start = new DateTime();  //NOW
                DateTime end = start.plus(p);

                int finalWinners = winners;
                String finalPrize = prize;
                String finalEmoji = emoji != null ? emoji : "\uD83C\uDF89";
                String finalColor = color;
                channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                    m.addReaction(finalEmoji).queue();
                    new Giveaway2(m, end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor).start();
                    CorgiBot.getInstance().getSql().registerGiveawayInSQL(member.getGuild().getId(), channel.getId(), m.getId(), start.getMillis(), end.getMillis(), finalPrize, finalWinners, finalEmoji, finalColor);
                });
                message.delete().reason("Start giveawaye").queue();
            }
        }
    }

    @Override
    public String getCommand() {
        return "giveaway";
    }

    @Override
    public String getDescription() {
        return "Chceš pořádat na serveru Giveaway? Tímto příkazem ho vytvoříš snadno!\nStačí pouze zaktivovat a počkat si na výherce!";
    }

    @Override
    public String getHelp() {
        return "%giveaway 1h30m | Výhra v loterii | 2 | :smile: | #fffff\n\nK podrovnější nápovědě napiš na serveru `%giveaway`";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"gw"};
    }

    private static Period getTimeFromInput(String input, MessageChannel channel) {
        try {
            return periodParser.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendErrorMessage("Byl zadán neplatný formát času! Zkus to třeba takto `1d` -> pro 1 den.",
                    channel);
            return null;
        }
    }
}

