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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserInfoCommand implements Command {


    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        String id;
        if (args.length != 1) {
            id = sender.getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        if (id.isEmpty()) {
            MessageUtils.sendErrorMessage("Musíš použít označení s @!", channel);
            return;
        }
        User user = CorgiBot.getJda().getUserById(id);
        if (user == null) {
            MessageUtils.sendErrorMessage("Nelze najít uživatele!", channel);
            return;
        }
        Member m2 = member.getGuild().getMember(user);
        channel.sendMessage(MessageUtils.getEmbed(sender, member.getGuild().getMember(user).getColor()).setThumbnail(user.getEffectiveAvatarUrl()).addField("Info o uživateli", "Uživatel: " + user.getName() + "#" + user.getDiscriminator() + " " + getDiscordRank(user)
                + "\nID: " + user.getId() + "\n" +
                "Avatar: " + (user.getEffectiveAvatarUrl() != null ? "[odkaz](" + user.getEffectiveAvatarUrl() + ')' : "Žádný") + "\n"
                + "Default Avatar: [odkaz](" + MessageUtils.getDefaultAvatar(sender) + ')' + "\n"
                + "Role: " + getRoles(m2, member.getGuild()), false)
                .addField("Časové data",
                        "Registrace: " + CorgiBot.getInstance().formatTime(LocalDateTime.from(user.getCreationTime())) + "\n" +
                                "Připojen: " + (member.getGuild().getMember(user) == null ? "Tento uživatel nebyl na tomto serveru!." : CorgiBot.getInstance().formatTime(LocalDateTime.from(member.getGuild().getMember(user).getJoinDate()))), false).build()).queue();

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
        return ".userinfo - Informace o sobě\n" +
                ".userinfo @nick - Informace o jiném uživateli";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ui"};
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
            return Constants.EMOTE_BOT;
        } else if (user.getId().equals("177516608778928129")) { //Wake
            return Constants.EMOTE_PARTNER;
        } else if (user.getId().equals("151332840577957889")) { //Liturkey
            return Constants.EMOTE_HYPESQUAD;
        } else if (user.getId().equals("263736235539955713")) { //_yyySepii
            return Constants.EMOTE_NITRO;
        } else {
            return "";
        }
    }
}
