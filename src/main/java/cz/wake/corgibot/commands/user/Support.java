package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Support implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.WHITE).setTitle("Corgi's support server")
                .setDescription("To connect to the support server, [**click here!**]({1})".replace("{1}", "https://discord.gg/pR2tj432NS")).build()).queue();
    }

    @Override
    public String getCommand() {
        return "support";
    }

    @Override
    public String getDescription() {
        return "Join Corgi's support server";
    }

    @Override
    public String getHelp() {
        return "%support - Get the URL";
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
