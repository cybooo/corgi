package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Support implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.WHITE).setTitle(I18n.getLoc(gw, "commands.support.title"))
                .setDescription(I18n.getLoc(gw, "commands.support.description".replace("{1}", "https://discordapp.com/invite/eaEFCYX"))).build()).queue();
    }

    @Override
    public String getCommand() {
        return "support";
    }

    @Override
    public String getDescription() {
        return "Získání odkazu na podporu Corgiho (server).";
    }

    @Override
    public String getHelp() {
        return "%support - Získání odkazu";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"podpora"};
    }
}
