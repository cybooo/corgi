package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "allowmusic",
        description = "Allows a role to perform a specific music command.",
        help = "%allowmusic <Command> <RoleId>",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}

)
@SinceCorgi(version = "1.3.5")
public class AllowMusic implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 0 || args.length == 1) {
            MessageUtils.sendErrorMessage("Wrong usage!\nUsage: " + gw.getPrefix() + "allowmusic <play/nowplaying/skip/stop/volume> <roleId>", channel);
        } else {
            try {
                if (args[0].equals("play") || args[0].equals("nowplaying") || args[0].equals("skip") || args[0].equals("stop") || args[0].equals("volume")) {
                    Long.parseLong(args[1]);
                    channel.sendMessage("Command **" + args[0] + "** allowed!").queue();
                    CorgiBot.getInstance().getSql().addRoleMusicCommand(message.getGuild().getId(), args[1], args[0]);
                } else {
                    channel.sendMessage("You need to specify a music command: **play** / **nowplaying** / **skip** / **stop** / **volume**!").queue();
                }
            } catch (NumberFormatException e1) {
                MessageUtils.sendErrorMessage("Role ID needs to be a number!", channel);
            } catch (Exception e2) {
                MessageUtils.sendErrorMessage("Oops.. Something wrong! (" + e2.getMessage() + ")", channel);
                e2.printStackTrace();
            }
        }
    }
}
