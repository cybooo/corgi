package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;

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
        if (id.isEmpty()){
            MessageUtils.sendErrorMessage("Musíš použít označení s @!", channel);
            return;
        }
        User user = CorgiBot.getJda().getUserById(id);
        if (user == null) {
            MessageUtils.sendErrorMessage("Nelze najít uživatele!", channel);
            return;
        }
        Member m2 = channel.getGuild().getMember(user);
        channel.sendMessage(MessageUtils.getEmbed(sender, channel.getGuild().getMember(user).getColor()).setThumbnail(user.getEffectiveAvatarUrl()).addField("Info o uživateli", "Uživatel: " + user.getName() + "#" + user.getDiscriminator()
                + "\nID: " + user.getId() + "\n" +
                "Avatar: " + (user.getEffectiveAvatarUrl() != null ? "[`odkaz`](" + user.getEffectiveAvatarUrl() + ')' : "Žádný") + "\n"
                + "Default Avatar: [`odkaz`](" + MessageUtils.getDefaultAvatar(sender) + ')' + "\n"
                + "Role: " +  getRoles(m2, channel.getGuild()), false)
                .addField("Časové data",
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

    private String getRoles(Member user, Guild guid){
        String roles = "";
        for(Role r : guid.getRoles()){
            if(user.getRoles().contains(r)){
                String role = r.getName();
                if(!role.equalsIgnoreCase("@everyone")){
                    roles += ", " + role;
                }
            }
        }
        if(roles.equals("")){
             roles = "Žádné";
        } else {
            roles=roles.substring(2);
        }
        return roles;
    }
}
