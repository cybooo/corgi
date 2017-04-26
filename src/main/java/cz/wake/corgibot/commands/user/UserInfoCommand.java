package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.LocalDateTime;

public class UserInfoCommand implements Command {


    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        String id;
        if (args.length != 1){
            id = sender.getId();
        } else {
            id = args[0].replaceAll("[^0-9]", "");
        }
        User user = CorgiBot.getJda().getUserById(id);
        if (user == null) {
            MessageUtils.sendErrorMessage("Nelze najít uživatele!", channel);
            return;
        }
        channel.sendMessage(MessageUtils.getEmbed(sender, Color.ORANGE).setThumbnail(user.getEffectiveAvatarUrl()).addField("Info o uživateli", "Uživatel: " + user.getName() + "#" + user.getDiscriminator()
                + "\nID: " + user.getId() + "\n" +
                "Avatar: " + (user.getEffectiveAvatarUrl() != null ? "[`link`](" + user.getEffectiveAvatarUrl() + ')' : "None") + "\n"
                + "Default Avatar: [`link`](" + MessageUtils.getDefaultAvatar(sender) + ')', false).addField("Časové data",
                "Registrace: " + CorgiBot.getInstance().formatTime(LocalDateTime.from(user.getCreationTime())) + "\n" +
                        "Připojen: " + (channel.getGuild().getMember(user) == null ? "Tento uživatel nebyl na tomto serveru!." : CorgiBot.getInstance().formatTime(LocalDateTime.from(channel.getGuild().getMember(user).getJoinDate()))), false).build()).queue();

    }

    @Override
    public String getCommand() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Info o uživateli";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
