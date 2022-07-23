package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@CommandInfo(
        name = "userinfo",
        aliases = {"ui"},
        description = "commands.userinfo.description",
        help = "commands.userinfo.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.2.3.2")
public class UserInfo implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String id;
        if (args.length != 1) {
            id = member.getUser().getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.userinfo.mention"), 10000, channel);
            return;
        }
        Member guildMember = member.getGuild().getMemberById(id);
        if (guildMember == null) {
            MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.userinfo.cant-view"), 10000, channel);
            return;
        }

        StringBuilder joinOrder = new StringBuilder();
        List<Member> joins = message.getGuild().getMemberCache().stream().sorted(Comparator.comparing(Member::getTimeJoined)).toList();
        int index = joins.indexOf(guildMember);
        index -= 3;
        if (index < 0) {
            index = 0;
        }
        joinOrder.append("\n");

        if (joins.get(index).equals(guildMember)) {
            joinOrder.append("[").append(joins.get(index).getEffectiveName()).append("]()");
        } else {
            joinOrder.append(joins.get(index).getEffectiveName());
        }

        for (int i = index + 1; i < index + 7; i++) {
            if (i >= joins.size()) {
                break;
            }
            Member usr = joins.get(i);
            String name = usr.getEffectiveName();
            if (usr.equals(guildMember))
                name = "[" + name + "](https://corgibot.xyz/)";
            joinOrder.append(" > ").append(name);
        }

        channel.sendMessageEmbeds(MessageUtils.getEmbed(member.getUser(), member.getGuild().getMember(guildMember.getUser()).getColor())
                .setThumbnail(guildMember.getUser().getEffectiveAvatarUrl())
                .addField(I18n.getLoc(gw, "commands.userinfo.real-name"), guildMember.getUser().getName() + "#" + guildMember.getUser().getDiscriminator() + " " + getDiscordRank(guildMember.getUser()), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.id"), guildMember.getUser().getId(), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.nickname"), guildMember.getEffectiveName(), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.registered"), CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getUser().getTimeCreated())), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.joined"), (member.getGuild().getMember(guildMember.getUser()) == null ? I18n.getLoc(gw, "commands.userinfo.user-not-found") : CorgiBot.getInstance().formatTime(LocalDateTime.from(member.getGuild().getMember(guildMember.getUser()).getTimeJoined()))), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.bot"), (guildMember.getUser().isBot() ? I18n.getLoc(gw, "internal.general.yes") : I18n.getLoc(gw, "internal.general.no")), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.boost"), guildMember.getTimeBoosted() != null ? CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getTimeBoosted())) : I18n.getLoc(gw, "commands.userinfo.no-boost"), true)
                .addField(I18n.getLoc(gw, "commands.userinfo.joined-order"), joinOrder.toString(), false)
                .addField(I18n.getLoc(gw, "commands.userinfo.roles"), getRoles(guildMember, member.getGuild(), gw), false).build()).queue();

    }

    private String getRoles(Member user, Guild guild, GuildWrapper gw) {
        StringBuilder roles = new StringBuilder();
        for (Role r : guild.getRoles()) {
            if (user.getRoles().contains(r)) {
                String role = r.getName();
                if (!role.equalsIgnoreCase("@everyone")) {
                    roles.append(", `").append(role).append("`");
                }
            }
        }
        if (roles.toString().equals("")) {
            roles = new StringBuilder(I18n.getLoc(gw, "commands.userinfo.no-roles"));
        } else {
            roles = new StringBuilder(roles.substring(2));
        }
        return roles.toString();
    }

    private String getDiscordRank(User user) {
        if (user.isBot()) {
            return EmoteList.EMOTE_BOT;
        } else if (user.getFlags().contains(User.UserFlag.PARTNER)) {
            return EmoteList.EMOTE_PARTNER;
        } else {
            return "";
        }
    }
}
