package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.ColorSelector;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class About implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        channel.sendMessage(MessageUtils.getEmbed(ColorSelector.getRandomColor()).setTitle("Představení Corgiho")
            .setDescription("Ahoj já jsem Corgi, jsem bot pro Discord servery.\nAktuálně moc funkcí nemám, ale časem se to určitě změní!\n\n" +
                    "**Web**: [http://corgibot.xyz](https://corgibot.xyz)\n" +
                    "**Invite**: [Odkaz](https://discordapp.com/oauth2/authorize?client_id=294952122582302720&scope=bot&permissions=104197334)\n" +
                    "**Vytvořil**: " + channel.getJDA().getUserById("177516608778928129").getAsMention())
                .setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl()).build()).queue();
    }

    @Override
    public String getCommand() {
        return "about";
    }

    @Override
    public String getDescription() {
        return "Představení Coriho a informace o něm";
    }

    @Override
    public String getHelp() {
        return "%about";
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
