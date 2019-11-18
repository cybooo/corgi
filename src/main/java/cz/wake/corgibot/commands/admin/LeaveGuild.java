package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.concurrent.TimeUnit;

@SinceCorgi(version = "1.2.0")
public class LeaveGuild implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("\u26A0 Potvrzení opuštění serveru \u26A0")
                    .setDescription("**VAROVÁNÍ**: Potvrzením následující akce Corgi opustí tento server!\nOpravdu chceš provést následující akci?").setFooter("Na potvrzení máš 60 vteřin.", null).build()).queue((Message m) -> {
                m.addReaction("\u2705").queue();
                m.addReaction("\u26D4").queue();
                message.delete().queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Potvrzení
                    return e.getUser().equals(member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals("\u2705"));
                }, (MessageReactionAddEvent ev) -> {
                    m.clearReactions().queue();
                    m.editMessage(MessageUtils.getEmbed(Constants.RED).setTitle("Potvrzení o opuštění!").setDescription("Corgi nyní opustí tento server! :sob:").build()).queue();
                    m.getGuild().leave().queue();
                }, 60, TimeUnit.SECONDS, null);

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { //Zrušení
                    return e.getUser().equals(member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getReactionEmote().getName().equals("\u26D4"));
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
    public CommandCategory getCategory() {
        return CommandCategory.ADMINISTARTOR;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }
}
