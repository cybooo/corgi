package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "allowmusic",
        description = "commands.allow-music.description",
        help = "commands.allow-music.help",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}
)
@SinceCorgi(version = "1.3.5")
public class AllowMusic implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 0 || args.length == 1) {
            MessageUtils.sendErrorMessage(String.format(I18n.getLoc(gw, "commands.allow-music.usage"), gw.getPrefix()), channel);
        } else {
            try {
                if (args[0].equals("play") || args[0].equals("nowplaying") || args[0].equals("skip") || args[0].equals("stop") || args[0].equals("volume")) {
                    Long.parseLong(args[1]);
                    channel.sendMessage(String.format(I18n.getLoc(gw, "commands.allow-music.command-allowed"), args[0])).queue();
                    CorgiBot.getInstance().getSql().addRoleMusicCommand(message.getGuild().getId(), args[1], args[0]);
                } else {
                    channel.sendMessage(I18n.getLoc(gw, "commands.allow-music.command-not-specified")).queue();
                }
            } catch (NumberFormatException e1) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.allow-music.role-id-number"), channel);
            } catch (Exception e2) {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "internal.error.command-failed"), channel);
                e2.printStackTrace();
            }
        }
    }
}
