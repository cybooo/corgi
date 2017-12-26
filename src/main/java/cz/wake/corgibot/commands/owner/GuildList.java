package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.menu.pagination.Paginator;
import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GuildList implements ICommand {

    private PaginatorBuilder pBuilder;

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {

        pBuilder = new PaginatorBuilder().setColumns(1)
                .setItemsPerPage(10)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException e) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(w)
                .setTimeout(1, TimeUnit.MINUTES);

        int page = 1;
        if (!(args.length < 1)) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                channel.sendMessage(MessageUtils.sendErrorMessage("`" + args[0] + "` je neni číslo!", channel)).queue();
                return;
            }
        }
        pBuilder.setItems(new String[0]);
        channel.getJDA().getGuilds().stream()
                .map(g -> "**" + g.getName() + "** (ID:" + g.getId() + ") ~ " + g.getMembers().size() + " členů")
                .forEach(s -> pBuilder.addItems(s));
        Paginator p = pBuilder.setColor(message.isFromType(ChannelType.TEXT) ? member.getGuild().getSelfMember().getColor() : Color.BLACK)
                .setText(":chart_with_upwards_trend: | Seznam Guild na kterých je **" + member.getGuild().getJDA().getSelfUser().getName() + "** připojen"
                        + (channel.getJDA().getShardInfo() == null ? ":" : "(Shard ID " + channel.getJDA().getShardInfo().getShardId() + "):"))
                .setUsers(message.getAuthor())
                .build();
        p.paginate(channel, page);

    }

    @Override
    public String getCommand() {
        return "guildlist";
    }

    @Override
    public String getDescription() {
        return "Přehled guild ve kterých Corgi je!";
    }

    @Override
    public String getHelp() {
        return "%guildlist <strana>";
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }


    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
