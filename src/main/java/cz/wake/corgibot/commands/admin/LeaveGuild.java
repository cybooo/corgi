package cz.wake.corgibot.commands.admin;

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
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

public class LeaveGuild implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("\u26A0 Potvrzení opuštění serveru \u26A0")
                    .setDescription("**VAROVÁNÍ**: Potvrzením následující akce Corgi opustí tento server!\nOpravdu chceš provést následující akci?").setFooter("Na potvrzení máš 60 vteřin.", null).build()).queue((Message m) -> {
                m.addReaction("\u2705").queue();
                m.addReaction("\u26D4").queue();
                message.delete().queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Potvrzení
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals("\u2705"));
                }, (MessageReactionAddEvent ev) -> {
                    m.clearReactions().queue();
                    m.editMessage(MessageUtils.getEmbed(Constants.RED).setTitle("Potvrzení o opuštění!").setDescription("Corgi nyní opustí tento server! :sob:").build()).queue();
                    m.getGuild().leave().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Zrušení
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals("\u26D4"));
                }, (MessageReactionAddEvent ev) -> {
                    m.editMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Opuštění zrušeno!").setDescription("Juchůůů! Corgi zde zůstane! :hugging:").build()).queue();
                    m.clearReactions().queue();
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    @Override
    public String getCommand() {
        return "leaveguild";
    }

    @Override
    public String getDescription() {
        return "Příkaz, kterým Corgi opustí server, pokud bude schválen. (Pouze Administrátoři)";
    }

    @Override
    public String getHelp() {
        return "%leaveguild";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTARTOR;
    }

    @Override
    public Rank getRank() {
        return Rank.ADMINISTRATOR;
    }
}
