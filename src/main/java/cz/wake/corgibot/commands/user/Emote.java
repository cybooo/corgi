package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@CommandInfo(
        name = "emote",
        aliases = {"emoji"},
        description = "This command displays the special ID of the selected Emote,\nor all Emote where Corgi is.",
        help = "%emote <regex|emote> - Emote info\n" +
                "%emote list - List all available Emotes for Corgi",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.8.1")
public class Emote implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Help - emote :question:")
                    .setDescription(getDescription() + "\n\n**Usage**\n" + getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].equalsIgnoreCase("list")) {
            if (member.getGuild().getEmotes().isEmpty()) {
                MessageUtils.sendAutoDeletedMessage("This server does not have any emotes!", 15000, channel);
            }
            StringBuilder builder = new StringBuilder("**Emotes:**\n");
            for (net.dv8tion.jda.api.entities.Emote e : member.getGuild().getEmotes()) {
                builder.append(" ").append(e.getAsMention());
            }
            channel.sendMessage(builder.toString()).queue();
        } else {
            String str = args[0];
            if (str.matches("<.*:.*:\\d+>")) { //Server Emotes
                String id = str.replaceAll("<.*:.*:(\\d+)>", "$1");
                long longId = Long.parseLong(id);
                net.dv8tion.jda.api.entities.Emote emote = channel.getJDA().getEmoteById(longId);
                if (emote == null) {
                    channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.RED).setTitle("**Unknown emote**")
                            .setDescription("**ID:** " + id + "\n" +
                                    "**Guild:** Unknown\n" +
                                    "**URL:** [Click me](https://cdn.discordapp.com/emojis/" + id + ".png)")
                            .setThumbnail("https://cdn.discordapp.com/emojis/" + id + ".png").build()).queue();
                    return;
                } else {
                    channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle("**Emote info** (" + emote.getName() + ")")
                            .setDescription("**ID:** " + emote.getId() + "\n" +
                                    "**Guild:** " + (emote.getGuild() == null ? "Unknown" : "" + emote.getGuild().getName() + "\n") +
                                    "**URL:** " + "[Click me](" + emote.getImageUrl() + ")").setThumbnail(emote.getImageUrl()).build()).queue();
                    return;
                }
            }
            if (str.codePoints().count() > 11) {
                MessageUtils.sendAutoDeletedMessage("Invalid emote or ID is too long!", 15000, channel);
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
            channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle("**Emote info**")
                    .setDescription(builder.toString()).build()).queue();
        }
    }

}
