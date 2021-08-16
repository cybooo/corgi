package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.Beta;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.Button;

@CommandInfo(
        name = "createticketembed",
        description = "Sends a embed message which allows users to open tickets.",
        help = "%createticketembed",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}

)
@Beta
@SinceCorgi(version = "1.3.6")
public class CreateTicketEmbed implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        message.delete().queue();

        if (!CorgiBot.getInstance().getSql().registeredTicketData(gw.getGuildId())) {
            CorgiBot.getInstance().getSql().registerTicketData(gw.getGuildId());
        }

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Tickets")
                .setDescription("Click " + EmoteList.ENVELOPE_WITH_ARROW + " to create a ticket!")
                .setColor(Constants.BLUE)
                .build()).setActionRow(Button.secondary("openticket", "Create ticket").withEmoji(Emoji.fromUnicode(EmoteList.ENVELOPE_WITH_ARROW)))
                .queue();
    }
}
