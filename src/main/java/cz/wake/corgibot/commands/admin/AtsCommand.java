package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.TimeUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AtsCommand implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - ats :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp()).build()).queue();
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (sender.getId().equals("177516608778928129") && member.isOwner()) {
                CorgiBot.getInstance().getSql().resetATS("surv_chat_body");
                CorgiBot.getInstance().getSql().resetATS("surv_played_time");
                CorgiBot.getInstance().getSql().resetATS("sky_chat_body");
                CorgiBot.getInstance().getSql().resetATS("sky_played_time");
                CorgiBot.getInstance().getSql().resetATS("crea_chat_body");
                CorgiBot.getInstance().getSql().resetATS("crea_played_time");
                CorgiBot.getInstance().getSql().resetATS("prison_chat_body");
                CorgiBot.getInstance().getSql().resetATS("prison_played_time");
                CorgiBot.getInstance().getSql().resetATS("vanilla_chat_body");
                CorgiBot.getInstance().getSql().resetATS("vanilla_played_time");
                CorgiBot.getInstance().getSql().resetATS("minigames_chat_body");
                CorgiBot.getInstance().getSql().resetATS("minigames_played_time");
            } else {
                MessageUtils.sendErrorMessage("Toto může provádět pouze Wake!", channel);
            }
        } else {
            String name = args[0];

            if (!CorgiBot.getInstance().getSql().isAT(name)) {
                MessageUtils.sendErrorMessage("Požadovaný člen není v AT nebo nebyl nalezen!", channel);
                return;
            }

            long opravnyCas = 7200000; //2h

            int survival_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "surv_chat_body");
            int survival_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "surv_played_time");
            long survival_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "surv_pos_aktivita") + opravnyCas;

            int skyblock_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "sky_chat_body");
            int skyblock_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "sky_played_time");
            long skyblock_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "sky_pos_aktivita")+ opravnyCas;

            int creative_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "crea_chat_body");
            int creative_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "crea_played_time");
            long creative_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "crea_pos_aktivita")+ opravnyCas;

            int prison_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "prison_chat_body");
            int prison_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "prison_played_time");
            long prison_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "prison_pos_aktivita")+ opravnyCas;

            int vanilla_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "vanilla_chat_body");
            int vanilla_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "vanilla_played_time");
            long vanilla_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "vanilla_pos_aktivita")+ opravnyCas;

            int minigames_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "minigames_chat_body");
            int minigames_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "minigames_played_time");
            long minigames_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "minigames_pos_aktivita")+ opravnyCas;

            int vanillasb_chat = CorgiBot.getInstance().getSql().getStalkerStats(name, "vanillasb_chat_body");
            int vanillasb_odehrano = CorgiBot.getInstance().getSql().getStalkerStats(name, "vanillasb_played_time");
            long vanillasb_posledni_aktivita = CorgiBot.getInstance().getSql().getStalkerStatsTime(name, "vanillasb_pos_aktivita")+ opravnyCas;

            int celkem_chat = survival_chat + skyblock_chat + creative_chat + prison_chat + vanilla_chat + minigames_chat + vanillasb_chat;
            int celkem_odehrano = survival_odehrano + skyblock_odehrano + creative_odehrano + prison_odehrano + vanilla_odehrano + minigames_odehrano + vanillasb_odehrano;


            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("ATS pro " + name)
                    .addField("Survival :evergreen_tree:", "**Chat**: " + survival_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", survival_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(survival_posledni_aktivita), true)
                    .addField("Skyblock :herb:", "**Chat**: " + skyblock_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", skyblock_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(skyblock_posledni_aktivita), true)
                    .addField("Creative :baby::skin-tone-1:", "**Chat**: " + creative_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", creative_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(creative_posledni_aktivita), true)
                    .addField("Prison :oncoming_police_car:", "**Chat**: " + prison_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", prison_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(prison_posledni_aktivita), true)
                    .addField("Vanilla :rose:", "**Chat**: " + vanilla_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", vanilla_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(vanilla_posledni_aktivita), true)
                    .addField("MiniGames :video_game:", "**Chat**: " + minigames_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", minigames_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(minigames_posledni_aktivita), true)
                    .addField("Vanilla Skyblock :jack_o_lantern:", "**Chat**: " + vanillasb_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", vanillasb_odehrano, false) + "\n" + "**Poslední aktivita**: " + getDate(vanillasb_posledni_aktivita), true)
                    .addField("Celkem :notepad_spiral:", "**Chat**: " + celkem_chat + "\n" + "**Odehráno**: " + TimeUtils.formatTime("%d dni, %hh %mm", celkem_odehrano, false), false)
                    .setFooter("Platné pro: " + getDate(System.currentTimeMillis() + opravnyCas), null).build()).queue((Message m) -> {

                m.addReaction("\u274C").queue();
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals("\u274C"));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    message.delete().queue();
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    @Override
    public String getCommand() {
        return "ats";
    }

    @Override
    public String getDescription() {
        return "Příkaz na zjištění aktivity AT na CM";
    }

    @Override
    public String getHelp() {
        return ".ats <nick> - Zjištění aktivity pro zadaný nick\n" +
                ".ats reset - Vyresetování ATS (Wake)";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public boolean onlyCM() {
        return true;
    }

    private String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final String timeString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
        return timeString;
    }

}
