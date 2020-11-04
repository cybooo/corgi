package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
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

@SinceCorgi(version = "1.2.3.2")
public class UserInfo implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String id;
        if (args.length != 1) {
            id = member.getUser().getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendAutoDeletedMessage("Musíš použít označení s @!", 10000, channel);
            return;
        }
        Member guildMember = member.getGuild().getMemberById(id);
        if (guildMember == null) {
            MessageUtils.sendAutoDeletedMessage("Nelze zobrazit informace o jiných uživatelích, než jsi ty sám.", 10000, channel);
            return;
        }

        StringBuilder joinOrder = new StringBuilder();
        List<Member> joins = message.getGuild().getMemberCache().stream().collect(Collectors.toList());
        joins.sort(Comparator.comparing(Member::getTimeJoined));
        int index = joins.indexOf(guildMember);
        index -= 3;
        if (index < 0)
            index = 0;
        joinOrder.append("\n");
        if (joins.get(index).equals(guildMember))
            joinOrder.append("[").append(joins.get(index).getEffectiveName()).append("]()");
        else
            joinOrder.append(joins.get(index).getEffectiveName());
        for (int i = index + 1; i < index + 7; i++) {
            if (i >= joins.size())
                break;
            Member usr = joins.get(i);
            String name = usr.getEffectiveName();
            if (usr.equals(guildMember))
                name = "[" + name + "](https://corgibot.xyz/)";
            joinOrder.append(" > ").append(name);
        }

        channel.sendMessage(MessageUtils.getEmbed(member.getUser(), member.getGuild().getMember(guildMember.getUser()).getColor())
                .setThumbnail(guildMember.getUser().getEffectiveAvatarUrl())
                .addField("Pravé jméno", guildMember.getUser().getName() + "#" + guildMember.getUser().getDiscriminator() + " " + getDiscordRank(guildMember.getUser()), true)
                .addField("ID", guildMember.getUser().getId(), true)
                .addField("Status", gameToString(guildMember.getActivities(), member), true)
                .addField("Nick", guildMember.getEffectiveName(), true)
                .addField("Registrován", CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getUser().getTimeCreated())), true)
                .addField("Připojen", (member.getGuild().getMember(guildMember.getUser()) == null ? "Tento uživatel nebyl na tomto serveru!." : CorgiBot.getInstance().formatTime(LocalDateTime.from(member.getGuild().getMember(guildMember.getUser()).getTimeJoined()))), true)
                .addField("Online stav", convertStatus(guildMember.getOnlineStatus()) + " " + guildMember.getOnlineStatus().name().toLowerCase().replaceAll("_", " "), true)
                .addField("Bot", (guildMember.getUser().isBot() ? "Ano" : "Ne"), true)
                .addField("Boost", guildMember.getTimeBoosted() != null ? CorgiBot.getInstance().formatTime(LocalDateTime.from(guildMember.getTimeBoosted())) : "Žádný boost", true)
                .addField("Pořadí připojení", joinOrder.toString(), false)
                .addField("Role", getRoles(guildMember, member.getGuild()), false).build()).queue();

    }

    @Override
    public String getCommand() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Získání základních informací o uživateli.";
    }

    @Override
    public String getHelp() {
        return "%userinfo - Informace o sobě\n" +
                "%userinfo @nick - Informace o jiném uživateli";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ui", "uzivatel"};
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    private String getRoles(Member user, Guild guid) {
        String roles = "";
        for (Role r : guid.getRoles()) {
            if (user.getRoles().contains(r)) {
                String role = r.getName();
                if (!role.equalsIgnoreCase("@everyone")) {
                    roles += ", `" + role + "`";
                }
            }
        }
        if (roles.equals("")) {
            roles = "Žádné";
        } else {
            roles = roles.substring(2);
        }
        return roles;
    }

    private String getDiscordRank(User user) {
        if (user.isBot()) {
            return EmoteList.EMOTE_BOT;
        } else if (user.getId().equals("177516608778928129")) { //Wake
            return EmoteList.EMOTE_PARTNER;
        } else {
            return "";
        }
    }

    private static String convertStatus(OnlineStatus status) {
        switch (status) {
            case ONLINE:
                return "<:online:314899088510418945>";
            case IDLE:
                return "<:away:314900395082252290>";
            case DO_NOT_DISTURB:
                return "<:dnd:314900395556339732>";
            default:
                return "<:offline:314900395430379521>";
        }
    }

    private static String gameToString(List<Activity> activities, Member member) {
        if (activities.size() == 0) {
            return "no game";
        }
        Activity g = member.getActivities().get(0);
        if (g == null) return "no game";

        String gameType = "Playing";

        switch (g.getType().getKey()) {
            case 1:
                gameType = "Streaming";
                break;
            case 2:
                gameType = "Listening to";
                break;
            case 3:
                gameType = "Watching";
        }

        String gameName = g.getName();
        return gameType + " " + gameName;
    }
}
