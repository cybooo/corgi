package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SinceCorgi(version = "1.0")
public class Purge implements ICommand {

    private final String CANCEL = "\u274C";
    private final Pattern LINK_PATTERN = Pattern.compile("https?:\\/\\/.+");
    private final String QUOTES_REGEX = "\"(.*?)\"";
    private final Pattern QUOTES_PATTERN = Pattern.compile(QUOTES_REGEX);

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        if (args.length < 1) {
            channel.sendMessage(MessageUtils.getEmbed(Constants.BLUE).setTitle("Zvol typ zpráv, který má být smazaný\n")
                    .setDescription(CleanType.ROBOT.getUnicode() + " **Boti**\n" + CleanType.EMBEDS.getUnicode() + " **Embeds**\n" + CleanType.LINKS.getUnicode() + " **Odkazy**\n" + CANCEL + " **Zrušení**").build()).queue((Message m) -> {
                for (CleanType type : CleanType.values())
                    m.addReaction(type.getUnicode()).queue();
                m.addReaction(CANCEL).queue();
                w.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent e) -> {
                    return e.getUser().equals(sender) && e.getMessageId().equals(m.getId()) && (e.getReaction().getEmote().getName().equals(CANCEL) || CleanType.of(e.getReaction().getEmote().getName()) != null);
                }, (MessageReactionAddEvent ev) -> {
                    m.delete().queue();
                    CleanType type = CleanType.of(ev.getReaction().getEmote().getName());
                    if (type != null)
                        executeClean(type.getText(), channel, message, " " + type.getText(), guildPrefix);
                }, 25, TimeUnit.SECONDS, () -> m.editMessage(MessageUtils.getEmbed(Constants.RED).setDescription("Čas vypršel!").build()).queue());
            });
        } else {
            try {
                int count = Integer.parseInt(args[0]) + 1;
                if (count < 2) {
                    MessageUtils.sendErrorMessage("Lze smazat nejméně 2 zprávy!", channel);
                }
                MessageHistory history = new MessageHistory(channel);
                int toRetrieve = count;
                while (history.getRetrievedHistory().size() < count) {
                    if (history.retrievePast(Math.min(toRetrieve, 100)).complete().isEmpty())
                        break;
                    toRetrieve -= Math.min(toRetrieve, 100);
                    if (toRetrieve < 2)
                        toRetrieve = 2;
                }
                int i = 0;
                List<Message> toDelete = new ArrayList<>();
                for (Message m : history.getRetrievedHistory()) {
                    if (m.getCreationTime().plusWeeks(2).isAfter(OffsetDateTime.now())) {
                        i++;
                        toDelete.add(m);
                    }
                    if (toDelete.size() == 100) {
                        for (Message mess : toDelete) {
                            channel.deleteMessageById(mess.getId()).queue();
                        }
                        toDelete.clear();
                    }
                }
                if (!toDelete.isEmpty()) {
                    if (toDelete.size() != 1) {
                        for (Message mess : toDelete) {
                            channel.deleteMessageById(mess.getId()).queue();
                        }
                    } else {
                        toDelete.forEach(mssage -> mssage.delete().complete());
                    }
                }
                channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(EmoteList.WARNING + " | Smazáno **" + i + "** zpráv.").build()).queue();

            } catch (Exception e) {
                executeClean(Arrays.toString(args), channel, message, null, guildPrefix);
            }
        }
    }

    protected void executeClean(String args, MessageChannel channel, Message m, String extra, String prefix) {
        List<String> texts = new ArrayList<>();
        Matcher ma = QUOTES_PATTERN.matcher(args);
        while (ma.find())
            texts.add(ma.group(1).trim().toLowerCase());
        String newArgs = args.replaceAll(QUOTES_REGEX, " ").toLowerCase();
        boolean all = newArgs.contains("vše");
        boolean bots = newArgs.contains("bots");
        boolean embeds = newArgs.contains("embeds");
        boolean links = newArgs.contains("odkazy");

        if (!all && !bots && !embeds && !links && texts.isEmpty() && m.getMentionedUsers().isEmpty()) {
            MessageUtils.sendErrorMessage("**Neplatný argumenty!**\nSprávné použití: " + prefix + "purge @uživatel | počet zpráv | `text` | bots | embeds | odkazy | vše\nVšechny typy mazání mohou smazat až 100 zpráv!", channel);
            return;
        }

        channel.getHistory().retrievePast(100).queue(messages -> {
            List<Message> toClean;
            if (all)
                toClean = messages;
            else {
                toClean = messages.stream().filter(mess -> {
                    String lowerCaseContent = mess.getRawContent().toLowerCase();
                    if (mess.getMentionedUsers().contains(mess.getAuthor()))
                        return true;
                    if (bots && mess.getAuthor().isBot())
                        return true;
                    if (embeds && !(mess.getEmbeds().isEmpty() && mess.getAttachments().isEmpty()))
                        return true;
                    if (links && LINK_PATTERN.matcher(mess.getRawContent()).find())
                        return true;
                    return texts.stream().anyMatch(str -> lowerCaseContent.contains(str));
                }).collect(Collectors.toList());
            }
            toClean.remove(m);
            if (toClean.isEmpty()) {
                MessageUtils.sendAutoDeletedMessage("Nebyly nalezeny žádné zprávy!", 10000, channel);
                return;
            }
            if (toClean.size() == 1)
                toClean.get(0).delete().queue(v -> channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":greenTick: | Smazáno **" + toClean.size() + "** zpráv.").build()).queue());
            else {
                try {
                    ((TextChannel) channel).deleteMessages(toClean).queue(v -> channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription(":greenTick: | Smazáno **" + toClean.size() + "** zpráv.").build()).queue());
                } catch (IllegalArgumentException e) {
                    MessageUtils.sendAutoDeletedMessage("Požadovaný výběr zpráv pro smazání je starší než 14 dní!", 10000, channel);
                }
            }
        });
    }

    @Override
    public String getCommand() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "Mazání zpráv botů, uživatelů a nebo všech.";
    }

    @Override
    public String getHelp() {
        return "%purge\n" +
                "%purge <číslo> - Maximum je 100 zpráv";
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

    @Override
    public Rank getRank() {
        return Rank.MODERATOR;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"clean"};
    }

    private enum CleanType {
        ROBOT("\uD83E\uDD16", "bots"),
        EMBEDS("\uD83D\uDDBC", "embeds"),
        LINKS("\uD83D\uDD17", "links");

        private final String unicode, text;

        CleanType(String unicode, String text) {
            this.unicode = unicode;
            this.text = text;
        }

        public String getUnicode() {
            return unicode;
        }

        public String getText() {
            return text;
        }

        public static CleanType of(String unicode) {
            for (CleanType type : values())
                if (type.getUnicode().equals(unicode))
                    return type;
            return null;
        }
    }
}
