package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

/*@SinceCorgi(version = "0.8.1")
public class Emote implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed().setTitle("Nápověda k příkazu - emote :question:")
                    .setDescription(getDescription() + "\n\n**Použití**\n" + getHelp().replace("%", gw.getPrefix())).build()).queue();
        } else if (args[0].equalsIgnoreCase("list")) {
            if (member.getGuild().getEmotes().isEmpty()) {
                MessageUtils.sendAutoDeletedMessage("Na tomto serveru nejsou žádné Emotes!", 15000, channel);
            }
            StringBuilder builder = new StringBuilder("**Přehled Emotes:**\n");
            for (net.dv8tion.jda.core.entities.Emote e : member.getGuild().getEmotes()) {
                builder.append(" ").append(e.getAsMention());
            }
            channel.sendMessage(builder.toString()).queue();
        } else {
            String str = args[0];
            if (str.matches("<:.*:\\d+>")) { //Server Emotes
                String id = str.replaceAll("<:.*:(\\d+)>", "$1");
                net.dv8tion.jda.core.entities.Emote emote = channel.getJDA().getEmoteById(id);
                if (emote == null) {
                    channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.RED).setTitle("**Neznámý Emote**")
                            .setDescription("**ID:** " + id + "\n" +
                                    "**Guild:** Neznamý\n" +
                                    "**Odkaz:** [ZDE](https://cdn.discordapp.com/emojis/" + id + ".png)")
                            .setThumbnail("https://cdn.discordapp.com/emojis/" + id + ".png").build()).queue();
                    return;
                } else {
                    channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle("**Info o Emote** (" + emote.getName() + ")")
                            .setDescription("**ID:** " + emote.getId() + "\n" +
                                    "**Guild:** " + (emote.getGuild() == null ? "Neznámý" : "" + emote.getGuild().getName() + "\n") +
                                    "**Odkaz:** " + emote.getImageUrl()).setThumbnail(emote.getImageUrl()).build()).queue();
                    return;
                }
            }
            if (str.codePoints().count() > 10) {
                MessageUtils.sendAutoDeletedMessage("Neplatný emote nebo ID je moc dlouhý!", 15000, channel);
                return;
            }
            StringBuilder builder = new StringBuilder(""); //Normalni emotes
            str.codePoints().forEachOrdered(code -> {
                char[] chars = Character.toChars(code);
                String hex = Integer.toHexString(code).toUpperCase();
                while (hex.length() < 4)
                    hex = "0" + hex;
                builder.append("\n`\\u").append(hex).append("`   ");
                if (chars.length > 1) {
                    String hex0 = Integer.toHexString(chars[0]).toUpperCase();
                    String hex1 = Integer.toHexString(chars[1]).toUpperCase();
                    while (hex0.length() < 4)
                        hex0 = "0" + hex0;
                    while (hex1.length() < 4)
                        hex1 = "0" + hex1;
                    builder.append("[`\\u").append(hex0).append("\\u").append(hex1).append("`]   ");
                }
                builder.append(String.valueOf(chars)).append("   _").append(Character.getName(code)).append("_");
            });
            channel.sendMessage(MessageUtils.getEmbed(member.getUser(), Constants.GREEN).setTitle("**Info o Emote**")
                    .setDescription(builder.toString()).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "emote";
    }

    @Override
    public String getDescription() {
        return "Tento příkaz zobrazí speciální ID vybraného Emote,\nnebo všech Emote, kde Corgi je.";
    }

    @Override
    public String getHelp() {
        return "%emote <regex|emote> - Info o Emote\n" +
                "%emote list - Seznam všech dostupných Emote pro Corgiho";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"emoji"};
    }
}*/
