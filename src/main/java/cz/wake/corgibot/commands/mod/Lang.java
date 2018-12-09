package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import cz.wake.corgibot.utils.lang.Language;
import cz.wake.corgibot.utils.lang.LanguageObject;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;

public class Lang implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        StringBuilder text = new StringBuilder();
        text.append("{1}\n\n".replace("{1}", I18n.getLoc(gw, "commands.language.description")));

        for(Language language : Language.values()) {
            if(language.getCode().equalsIgnoreCase(gw.getLanguage())){
                text.append("• " + language.getFlag() + " **" + language.getNativeName() + "** [{1}]\n".replace("{1}", I18n.getLoc(gw, "commands.language.selected")));
            } else {
                text.append("• " + language.getFlag() + " " + language.getNativeName() + "\n");
            }
        }

        channel.sendMessage(MessageUtils.getEmbed(Color.BLACK).setTitle(I18n.getLoc(gw,"commands.language.title")).setDescription(text)
                .setFooter(I18n.getLoc(gw,"commands.language.footer"), null).build()).queue((Message m) -> {
            m.addReaction(EmoteList.ENGLISH_FLAG).queue();
            m.addReaction(EmoteList.CZECH_FLAG).queue();
            m.addReaction(EmoteList.SLOVAK_FLAG).queue();
        });
    }

    @Override
    public String getCommand() {
        return "language";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"lang","jazyk"};
    }
}
