package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@CommandInfo(
        name = "emote",
        aliases = {"emoji"},
        description = "commands.emote.description",
        help = "commands.emote.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.8.1")
public class Emote implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessageEmbeds(MessageUtils.getEmbed().setTitle(I18n.getLoc(gw, "commands.emote.embed-title"))
                    .setDescription(getDescription() + "\n\n**" + I18n.getLoc(gw, "internal.general.usage") + "**\n" + getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].equalsIgnoreCase("list")) {
            if (member.getGuild().getEmojis().isEmpty()) {
                MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.emote.no-emote"), 15000, channel);
            }
            StringBuilder builder = new StringBuilder("**" + I18n.getLoc(gw, "commands.emote.emotes") + ":**\n");
            for (Emoji e : member.getGuild().getEmojis()) {
                builder.append(" ").append(e.getFormatted());
            }
            channel.sendMessage(builder.toString()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) { //Server Emotes
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                long longId = Long.parseLong(id);
                CustomEmoji emote = channel.getJDA().getEmojiById(longId);
                if (emote == null) {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(member.getUser(), Constants.RED).setTitle(I18n.getLoc(gw, "commands.emote.unknown-emote"))
                            .setDescription(I18n.getLoc(gw, "commands.emote.id") + id + "\n" +
                                    I18n.getLoc(gw, "commands.emote.guild") + I18n.getLoc(gw, "commands.emote.unknown") + "\n" +
                                    I18n.getLoc(gw, "commands.emote.url") + "[" + I18n.getLoc(gw, "commands.emote.click-me") + "](https://cdn.discordapp.com/emojis/" + id + ".png)")
                            .setThumbnail("https://cdn.discordapp.com/emojis/" + id + ".png").build()).queue();
                } else {
                    channel.sendMessageEmbeds(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle("**Emote info** (" + emote.getName() + ")")
                            .setDescription(I18n.getLoc(gw, "commands.emote.id") + emote.getId() + "\n" +
                                    I18n.getLoc(gw, "commands.emote.guild") + (message.getGuild() == null ? I18n.getLoc(gw, "commands.emote.unknown") : "" + message.getGuild().getName() + "\n") +
                                    I18n.getLoc(gw, "commands.emote.url") + "[" + I18n.getLoc(gw, "commands.emote.click-me") + "](" + emote.getImageUrl() + ")").setThumbnail(emote.getImageUrl()).build()).queue();
                }
                return;
            }
            if (str.codePoints().count() > 11) {
                MessageUtils.sendAutoDeletedMessage(I18n.getLoc(gw, "commands.emote.invalid-or-long"), 15000, channel);
                return;
            }
            StringBuilder builder = new StringBuilder(); //Normal emotes
            str.codePoints().forEachOrdered(code -> {
                char[] chars = Character.toChars(code);
                StringBuilder hex = new StringBuilder(Integer.toHexString(code).toUpperCase());
                while (hex.length() < 4)
                    hex.insert(0, "0");
                builder.append("\n`\\u").append(hex).append("`   ");
                if (chars.length > 1) {
                    StringBuilder hex0 = new StringBuilder(Integer.toHexString(chars[0]).toUpperCase());
                    StringBuilder hex1 = new StringBuilder(Integer.toHexString(chars[1]).toUpperCase());
                    while (hex0.length() < 4)
                        hex0.insert(0, "0");
                    while (hex1.length() < 4)
                        hex1.insert(0, "0");
                    builder.append("[`\\u").append(hex0).append("\\u").append(hex1).append("`]   ");
                }
                builder.append(String.valueOf(chars)).append("   _").append(Character.getName(code)).append("_");
            });
            channel.sendMessageEmbeds(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle(I18n.getLoc(gw, "commands.emote.emote-info"))
                    .setDescription(builder.toString()).build()).queue();
        }
    }

}
