package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

@CommandInfo(
        name = "bigmoji",
        aliases = {"animoji"},
        description = "commands.bigboji.description",
        help = "commands.bigmoji.help",
        category = CommandCategory.FUN
)
public class Bigmoji implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed().setTitle(I18n.getLoc(gw, "internal.general.help-command") + " - bigmoji :question:")
                    .setDescription(getDescription() + "\n\n**" + I18n.getLoc(gw, "internal.general.usage") + "**\n" +
                            getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) {
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                long longId = Long.parseLong(id);
                RichCustomEmoji emote = channel.getJDA().getEmojiById(longId);
                if (emote != null) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed().setImage(emote.getImageUrl()).build()).queue();
                } else {
                    MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.bigmoji.only-server-emoji"), channel);
                }
            } else {
                MessageUtils.sendErrorMessage(I18n.getLoc(gw, "commands.bigmoji.invalid-emoji-format"), channel);
            }
        }
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }
}
