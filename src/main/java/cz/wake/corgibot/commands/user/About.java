package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@SinceCorgi(version = "0.1")
public class About implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (message.getContentRaw().contains("corgi")) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.DEFAULT_PURPLE).setTitle("Introducing Corgi!")
                    .setDescription(
                            "Hey, i'm Corgi! I'm a bot for Discord!\n" +
                                    "I'm designed for a wide range of functionality from administration, obtaining statistics from games to classic commands for Discord!\n" +
                                    "I was originally created by Wake#0001, but now i'm maintained by cybo#0001!\n\n" +

                                    "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                                    "**Invite me**: [Click me](https://discord.com/api/oauth2/authorize?client_id=860244075138383922&permissions=3220700791&scope=bot)\n" +
                                    "**Owner**: " + channel.getJDA().getUserById("485434705903222805").getAsMention() + "\n" +
                                    "**Suppport Guild**: [Click me](https://discord.gg/pR2tj432NS)")
                    .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl())
                    .setImage("https://www.cyborggg.eu/images/corgi_logo.jpg").build()).queue();
        } else {
            channel.sendMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Introducing Corgi!")
                    .setDescription(
                            "Hey, i'm Corgi! I'm a bot for Discord!\n" +
                                    "I'm designed for a wide range of functionality from administration, obtaining statistics from games to classic commands for Discord!\n" +
                                    "I was originally created by Wake#0001, but now i'm maintained by cybo#0001!\n\n" +

                                    "**Web**: [https://corgibot.xyz](https://corgibot.xyz)\n" +
                                    "**Invite me**: [Click me](https://discord.com/api/oauth2/authorize?client_id=860244075138383922&permissions=3220700791&scope=bot)\n" +
                                    "**Owner**: " + channel.getJDA().getUserById("485434705903222805").getAsMention() + "\n" +
                                    "**Suppport Guild**: [Click me](https://discord.gg/pR2tj432NS)")
                    .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "about";
    }

    @Override
    public String getDescription() {
        return "About Corgi";
    }

    @Override
    public String getHelp() {
        return "%about - Shows basic information about Corgi";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"info", "binfo", "corgi"};
    }
}
