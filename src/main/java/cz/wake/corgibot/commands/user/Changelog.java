package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class Changelog implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setTitle("Changelog :page_facing_up: ").setDescription("Seznam změn nalezneš [**ZDE**](https://changelog.corgibot.xyz/)").build()).queue();
    }

    @Override
    public String getCommand() {
        return "changelog";
    }

    @Override
    public String getDescription() {
        return "Získej přehled o posledních změnách";
    }

    @Override
    public String getHelp() {
        return "%changelog";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
