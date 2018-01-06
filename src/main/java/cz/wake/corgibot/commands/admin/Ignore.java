package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.menu.pagination.Paginator;
import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SinceCorgi(version = "1.2.0")
public class Ignore implements ICommand {

    private PaginatorBuilder pBuilder;

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if(args.length < 1){
            channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignorování channelu: " + channel.getName())
                    .setDescription("Zakáže používání Corgiho příkazů v tomto channelu.\nPokuď budeš chtít ignorování zrušit, stačí napsat opět příkaz `" + guildPrefix + "ignore` a ignorování zrušit.\n\n" +
                            ":one: | " + formatTruth(channel) + " ignorování tohoto channelu!\n:two: | Pro zobrazení seznamu všech ignorovaných channelů").setFooter("Pokud chceš akci odvolat nereaguj na ní, do 30 vteřin se zruší!", null).build()).queue((Message m) -> {
                m.addReaction(EmoteList.ONE).queue();
                m.addReaction(EmoteList.TWO).queue();

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 1
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(EmoteList.ONE));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    ignoreChannel(channel, member, guildPrefix);
                }, 60, TimeUnit.SECONDS, () -> m.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription("Čas vypršel!").build()));

                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // 2
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(EmoteList.TWO));
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    shopIgnoredChannels(channel,member,w);
                }, 60, TimeUnit.SECONDS, null);
            });
        }
    }

    @Override
    public String getCommand() {
        return "ignore";
    }

    @Override
    public String getDescription() {
        return "Příkaz k nastavení ignorování veškerých příkazů v požadovaném channelu.";
    }

    @Override
    public String getHelp() {
        return "%ignore - Nastavení ignorování";
    }

    @Override
    public CommandType getType() {
        return CommandType.ADMINISTARTOR;
    }

    @Override
    public Rank getRank() {
        return Rank.ADMINISTRATOR;
    }

    private boolean getTruth(MessageChannel channel){
        return CorgiBot.getIgnoredChannels().isBlocked(channel);
    }

    private String formatTruth(MessageChannel channel){
        boolean truth = getTruth(channel);
        if(truth){
            return "Zakázat";
        }
        return "Povolit";
    }

    private void ignoreChannel(MessageChannel channel, Member member, String prefix){
        try {
            TextChannel ch = member.getGuild().getTextChannelById(channel.getId());
            if(CorgiBot.getIgnoredChannels().getIgnoredChannels().containsValue(ch)){
                CorgiBot.getIgnoredChannels().set(member.getGuild(),ch);
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Ignorování channlu: " + channel.getName())
                        .setDescription("\uD83D\uDD14 | Corgi naslouchá všem svým příkazům v tomto channelu!")
                        .setFooter("Ignorování povolíš opět pomocí `" + prefix + "ignore`", null).build()).queue();
                return;
            }
            CorgiBot.getIgnoredChannels().set(member.getGuild(),ch);
            channel.sendMessage(MessageUtils.getEmbed(Constants.ORANGE).setTitle("Ignorování channlu: " + channel.getName())
                    .setDescription("\uD83D\uDD15 | Corgi od teď ignoruje veškeré své příkazy v tomto channelu!")
                    .setFooter("Ignorování zrušíš opět pomocí `" + prefix + "ignore`", null).build()).queue();
        } catch (Exception e){
            MessageUtils.sendAutoDeletedMessage("Nastala chyba při privádění operace! Zkus to později.", 35000L, channel);
        }

    }

    private void shopIgnoredChannels(MessageChannel channel, Member member, EventWaiter w){
        List<TextChannel> channels = CorgiBot.getIgnoredChannels().getIgnoredGuildChannels(member);

        if(channels.isEmpty()){
            MessageUtils.sendErrorMessage("Nemáš nastavený žádný ignorovaný channel!", channel);
            return;
        }

        pBuilder = new PaginatorBuilder().setColumns(1)
                .setItemsPerPage(10)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException e) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(w)
                .setTimeout(1, TimeUnit.MINUTES);

        for(MessageChannel m : channels){
            pBuilder.addItems(m.getName());
        }

        Paginator p = pBuilder.setColor(Constants.BLUE).setText("Seznam ignorovaných channelů:").build();
        p.paginate(channel,1);

    }
}
