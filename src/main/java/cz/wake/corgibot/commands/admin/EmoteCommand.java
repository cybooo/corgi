package cz.wake.corgibot.commands.admin;

import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.*;

public class EmoteCommand implements Command {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("**Použítí příkazu .emote**")
                    .setDescription("**.emote** - Zobrazí tuto nápovědu\n" +
                            "**.emote <regex|emote>** - Zobrazí info o Emote\n" +
                            "**.emote list** - Zobrazí seznam všech dostupných Emotes").build()).queue();
        } else if (args[0].equalsIgnoreCase("list")) {
            if (member.getGuild().getEmotes().isEmpty()) {
                MessageUtils.sendErrorMessage("Na tomto serveru nejsou žádné Emotes!", channel);
            }
            StringBuilder builder = new StringBuilder("**Přehled Emotes:**\n");
            for (Emote e : member.getGuild().getEmotes()) {
                builder.append(" ").append(e.getAsMention());
            }
            channel.sendMessage(builder.toString()).queue();
        } else {
            String str = (String) args[0];
            if (str.matches("<:.*:\\d+>")) { //Server Emotes
                String id = str.replaceAll("<:.*:(\\d+)>", "$1");
                Emote emote = channel.getJDA().getEmoteById(id);
                if (emote == null) {
                    channel.sendMessage(MessageUtils.getEmbed(sender, Constants.RED).setTitle("**Neznámý Emote**")
                            .setDescription("**ID:** " + id + "\n" +
                                    "**Guild:** Neznamý\n" +
                                    "**Odkaz:** https://discordcdn.com/emojis/" + id + ".png")
                            .setThumbnail("https://discordcdn.com/emojis/" + id + ".png").build()).queue();
                    return;
                } else {
                    channel.sendMessage(MessageUtils.getEmbed(sender, Constants.GREEN).setTitle("**Info o Emote** (" + emote.getName() + ")")
                            .setDescription("**ID:** " + emote.getId() + "\n" +
                                    "**Guild:** " + (emote.getGuild() == null ? "Neznámý" : "" + emote.getGuild().getName() + "\n") +
                                    "**Odkaz:** " + emote.getImageUrl()).setThumbnail(emote.getImageUrl()).build()).queue();
                    return;
                }
            }
            if (str.codePoints().count() > 10) {
                MessageUtils.sendErrorMessage("Neplatný emote nebo ID je moc dlouhý!", channel);
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
            channel.sendMessage(MessageUtils.getEmbed(sender, Constants.GREEN).setTitle("**Info o Emote**")
                    .setDescription(builder.toString()).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "emote";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.GUILD;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
