package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "prefix",
        aliases = {"setprefix"},
        description = "Set a custom prefix for this server",
        help = "%prefix reset/[prefix]",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_SERVER}
)
@SinceCorgi(version = "1.2.0")
public class SetPrefix implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 1) {
            if (CorgiBot.isIsBeta()) {
                channel.sendMessage("Corgi is in Beta, the prefix cannot be changed!").queue();
                return;
            }
            if (args[0].equalsIgnoreCase("reset")) {
                gw.setPrefix("c!", true);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription("Prefix was changed back to `c!`").build()).queue();
            } else if (args[0].length() < 4) {
                gw.setPrefix(args[0], true);
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.GREEN).setDescription(String.format("Prefix was set to `%s`", args[0])).build()).queue();
            } else {
                MessageUtils.sendErrorMessage("Prefix can't be longer than 3 characters!", channel);
            }
        } else {
            channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setDescription(String.format("The current prefix is `%s`", gw.getPrefix())).build()).queue();
        }
    }

}
