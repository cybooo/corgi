package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.lang.Language;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "language",
        aliases = {"lang", "jazyk"},
        category = CommandCategory.MODERATION,
        userPerms = {Permission.MANAGE_CHANNEL}

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

        channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(I18n.getLoc(gw, "commands.language.title")).setDescription(text)
                .setFooter(I18n.getLoc(gw, "commands.language.footer"), null).build()).queue((Message m) -> {
            m.addReaction(Emoji.fromUnicode(EmoteList.ENGLISH_FLAG)).queue();
            m.addReaction(Emoji.fromUnicode(EmoteList.CZECH_FLAG)).queue();

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // ENGLISH
                return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals(EmoteList.ENGLISH_FLAG));
            }, (MessageReactionAddEvent ev) -> {
                gw.setLanguage(Language.EN_US.getCode(), true);
                m.delete().queue();
            }, 60, TimeUnit.SECONDS, null);

            w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> { // CZECH
                return Objects.equals(e.getUser(), member.getUser()) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmoji().getName().equals(EmoteList.CZECH_FLAG));
            }, (MessageReactionAddEvent ev) -> {
                gw.setLanguage(Language.CZ_CS.getCode(), true);
                m.delete().queue();
            }, 60, TimeUnit.SECONDS, null);

        });

    }

}
