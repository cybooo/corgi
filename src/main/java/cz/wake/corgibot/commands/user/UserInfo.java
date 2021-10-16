package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
        name = "userinfo",
        aliases = {"ui"},
        description = "Get basic info about a user",
        help = "%userinfo - Information about yourself\n" +
                "%userinfo @nick - Information about someone else",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "1.2.3.2")
public class UserInfo implements CommandBase {

    private static String convertStatus(OnlineStatus status) {
        return switch (status) {
            case ONLINE -> "<:online:860297938911887370>";
            case IDLE -> "<:away:86029793893639785>";
            case DO_NOT_DISTURB -> "<:dnd:860297938897731604>";
            default -> "<:offline:860297938873614378>";
        };
    }

    private static String gameToString(List<Activity> activities, Member member) {
        if (activities.size() == 0) {
            return "no game";
        }
        Activity g = member.getActivities().get(0);
        if (g == null) return "no game";

        String gameType = switch (g.getType().getKey()) {
            case 1 -> "Streaming";
            case 2 -> "Listening to";
            case 3 -> "Watching";
            default -> "Playing";
        };

        String gameName = g.getName();
        return gameType + " " + gameName;
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String id;
        if (args.length != 1) {
            id = member.getUser().getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendAutoDeletedMessage("You need to mention someone!", 10000, channel);
            return;
        }
        Member guildMember = member.getGuild().getMemberById(id);
        if (guildMember == null) {
            MessageUtils.sendAutoDeletedMessage("Unable to view information for users other than yourself.", 10000, channel);
            return;
        }

        StringBuilder joinOrder = new StringBuilder();
        List<Member> joins = message.getGuild().getMemberCache().stream().sorted(Comparator.comparing(Member::getTimeJoined)).collect(Collectors.toList());
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
                .addField("Real name", guildMember.getUser().getName() + "#" + guildMember.getUser().getDiscriminator() + " " + getDiscordRank(guildMember.getUser()), true)
                .addField("ID", guildMember.getUser().getId(), true)
                .addField("Status", gameToString(guildMember.getActivities(), member), true)
                .addField("Nickname", guildMember.getEffectiveName(), true)
                .addField("Registered", CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getUser().getTimeCreated())), true)
                .addField("Joined", (member.getGuild().getMember(guildMember.getUser()) == null ? "This user was not found on this server!." : CorgiBot.getInstance().formatTime(LocalDateTime.from(member.getGuild().getMember(guildMember.getUser()).getTimeJoined()))), true)
                .addField("Status", convertStatus(guildMember.getOnlineStatus()) + " " + guildMember.getOnlineStatus().name().toLowerCase().replaceAll("_", " "), true)
                .addField("Bot", (guildMember.getUser().isBot() ? "Yes" : "No"), true)
                .addField("Boost", guildMember.getTimeBoosted() != null ? CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getTimeBoosted())) : "No boost", true)
                .addField("Joined order", joinOrder.toString(), false)
                .addField("Roles", getRoles(guildMember, member.getGuild()), false).build()).queue();

    }

    private String getRoles(Member user, Guild guid) {
        StringBuilder roles = new StringBuilder();
        for (Role r : guid.getRoles()) {
            if (user.getRoles().contains(r)) {
                String role = r.getName();
                if (!role.equalsIgnoreCase("@everyone")) {
                    roles.append(", `").append(role).append("`");
                }
            }
        }
        if (roles.toString().equals("")) {
            roles = new StringBuilder("No roles");
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
