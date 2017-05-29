package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class PlayerStatsCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length < 1) {
            channel.sendMessage(sender.getAsMention() + " Musíš napsat nick, zatím neumím číst myšlenky!").queue();
        } else {
            String name = args[0];
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setDescription("Generuji...").build()).queue(m -> {
                m.editMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle(name + "\n\n", null).setDescription("**Měna**\n" +
                        "CraftCoins: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerCoins(name) + " CC\n" +
                        "SkyDust: " + String.valueOf(CorgiBot.getInstance().getSql().getPlayerSkyDust(name))))
                        .setThumbnail("https://crafatar.com/renders/body/" + name + "?overlay").build()).queue();
            });
        }
    }

    @Override
    public String getCommand() {
        return "pstats";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ps"};
    }

    @Override
    public String getDescription() {
        return "Statistiky ze serveru";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
