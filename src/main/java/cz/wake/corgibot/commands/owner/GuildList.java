package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.pagination.old.Paginator;
import cz.wake.corgibot.utils.pagination.old.PaginatorBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@OnlyOwner
@CommandInfo(
        name = "guildlist",
        aliases = {"servers", "serverlist"},
        description = "Shows guild list!",
        help = "%guildlist <strana>",
        category = CommandCategory.BOT_OWNER
)
@SinceCorgi(version = "1.0")
public class GuildList implements CommandBase {

    private PaginatorBuilder pBuilder;

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

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
                MessageUtils.sendErrorMessage("`" + args[0] + "` is not a number!", channel);
                return;
            }
        }
        pBuilder.setItems();
        channel.getJDA().getGuilds().stream()
                .map(g -> "**" + g.getName() + "** (ID:" + g.getId() + ") ~ " + g.getMembers().size() + " členů")
                .forEach(s -> pBuilder.addItems(s));
        Paginator p = pBuilder.setColor(message.isFromType(ChannelType.TEXT) ? member.getGuild().getSelfMember().getColor() : Color.BLACK)
                .setText(":chart_with_upwards_trend: | Guilds where **" + member.getGuild().getJDA().getSelfUser().getName() + "** is"
                        + (channel.getJDA().getShardInfo() == null ? ":" : "(Shard ID " + channel.getJDA().getShardInfo().getShardId() + "):"))
                .setUsers(message.getAuthor())
                .build();
        p.paginate(channel, page);

    }
}
