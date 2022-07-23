package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "prefix",
        aliases = {"setprefix"},
        description = "commands.prefix.description",
        help = "commands.prefix.help",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.2.0")
public class SetPrefix implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 1) {
            if (CorgiBot.isBeta()) {
                channel.sendMessage(I18n.getLoc(gw, "commands.prefix.corgi-is-beta-prefix")).queue();
                return;
            }
            if (args[0].equalsIgnoreCase("reset")) {
                gw.setPrefix("c!", true);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(I18n.getLoc(gw, "commands.prefix.reset-back")).build()).queue();
            } else if (args[0].length() < 4) {
                gw.setPrefix(args[0], true);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format(I18n.getLoc(gw, "commands.prefix.prefix-set-to"), args[0])).build()).queue();
            } else {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.prefix.cannot-three-chars"), channel);
            }
        } else {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(String.format(I18n.getLoc(gw, "commands.prefix.current-prefix-is"), gw.getPrefix())).build()).queue();
        }
    }

}
