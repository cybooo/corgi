package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.lang.Language;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;

@CommandInfo(
        name = "language",
        aliases = {"lang", "jazyk"},
        category = CommandCategory.MODERATION
)
public class Lang implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        StringBuilder text = new StringBuilder();
        text.append("{1}\n\n".replace("{1}", I18n.getLoc(gw, "commands.language.description")));

        for (Language language : Language.values()) {
            if (language.getCode().equalsIgnoreCase(gw.getLanguage())) {
                text.append("• ").append(language.getFlag()).append(" **").append(language.getNativeName()).append("** [{1}]\n".replace("{1}", I18n.getLoc(gw, "commands.language.selected")));
            } else {
                text.append("• ").append(language.getFlag()).append(" ").append(language.getNativeName()).append("\n");
            }
        }

        channel.sendMessage(MessageUtils.getEmbed(Color.BLACK).setTitle(I18n.getLoc(gw, "commands.language.title")).setDescription(text)
                .setFooter(I18n.getLoc(gw, "commands.language.footer"), null).build()).queue((Message m) -> {
            m.addReaction(EmoteList.ENGLISH_FLAG).queue();
            m.addReaction(EmoteList.CZECH_FLAG).queue();
            m.addReaction(EmoteList.SLOVAK_FLAG).queue();
        });
    }

}
