package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PlayerStatsCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            channel.sendMessage(sender.getAsMention() + " Musíš napsat nick, zatím neumím číst myšlenky!").queue();
        } else {
            String name = args[0];
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.editMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Statistiky z celého serveru (ALPHA TEST)")
                        .addField("Základní informace :books:",
                                "**Nick**: " + name + "\n" +
                                "**UUID**: Nenazeleno\n" +
                                "**První připojení**: Nenalezeno\n" +
                                "**Poslední připojení**: Nenalezeno\n" +
                                "**Poslední server**: Nenalezeno", false)
                        .addField("Měna :moneybag: ",
                                "**CraftCoins**: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerBalance(name, "balance") + " CC\n" +
                                "**SkyDust**: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerBalance(name, "skykeys"))), false)
                        .addField("Hlasování :ballot_box:",
                                "**Tento týden**: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerVotes(name, "week")) + "\n" +
                                "**Tento měsíc**: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerVotes(name, "month")) + "\n" +
                                "**Celkem**: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerVotes(name, "votes")) + "\n" +
                                "**Další možný hlas**: " + String.valueOf(getDate(CorgiBot.getInstance().getSql().getPlayerNextVote(name) + 7200000)), false)
                        .build()).queue();
                m.addReaction(Constants.BACK).queue();
                m.addReaction(Constants.NEXT).queue();
            });
        }
    }

    private void showPage(Message message, int page, String name){
        switch(page){
            case 1:
                //Bbldw
                break;
        }
    }

    @Override
    public String getCommand() {
        return "pstats";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ps"};
    }

    @Override
    public boolean onlyCM() {
        return true;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }
}
