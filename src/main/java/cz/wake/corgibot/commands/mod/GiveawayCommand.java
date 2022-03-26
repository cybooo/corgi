package cz.wake.corgibot.commands.mod;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.annotations.Optional;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.annotations.AppOption;
import com.freya02.botcommands.api.application.slash.GuildSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.managers.Giveaway2;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

@CommandMarker
@SinceCorgi(version = "2.3.2")
public class GiveawayCommand extends ApplicationCommand {

    private static final PeriodFormatter PERIOD_FORMATTER = new PeriodFormatterBuilder()
            .appendDays().appendSuffix("d")
            .appendHours().appendSuffix("h")
            .appendMinutes().appendSuffix("m")
            .appendSeconds().appendSuffix("s")
            .toFormatter();

    @JDASlashCommand(
            name = "giveaway",
            description = "Creates a giveaway."
    )
    public void execute(GuildSlashEvent event,
                        @AppOption(name = "length", description = "How long should the giveaway be?") String length,
                        @AppOption(name = "prize", description = "What's the prize?") String prize,
                        @AppOption(name = "winners", description = "How many winner should there be?") long winners,
                        @Optional @AppOption(name = "emoji", description = "What emoji should be used as a reaction?") String emoji,
                        @Optional @AppOption(name = "color", description = "What HEX Color should be used?") String hexColor) {

        Period p = getTimeFromInput(length, event);
        DateTime start = new DateTime();
        DateTime end = start.plus(p);
        long time = end.getMillis() - start.getMillis();

        if (time < 150000) {
            event.reply("Giveaways can't be shorter than 3 minutes!").setEphemeral(true).queue();
            return;
        }

        String finalEmoji = emoji != null ? emoji : "🎉";
        event.getChannel().sendMessageEmbeds(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generating..").build()).queue(m -> {
            m.addReaction(finalEmoji).queue();
            new Giveaway2(m, end.getMillis(), prize, winners, finalEmoji, hexColor).start();
            CorgiBot.getInstance().getSql().registerGiveawayInSQL(event.getGuild().getId(), event.getChannel().getId(), m.getId(), start.getMillis(), end.getMillis(), prize, winners, finalEmoji, hexColor);
        });
        event.reply("Giveaway started!").setEphemeral(true).queue();

    }

    private static Period getTimeFromInput(String input, GuildSlashEvent event) {
        try {
            return PERIOD_FORMATTER.parsePeriod(input);
        } catch (IllegalArgumentException e) {
            event.reply("Invalid time format! Try this: `1d` -> for one day.").setEphemeral(true).queue();
            return null;
        }
    }

}
