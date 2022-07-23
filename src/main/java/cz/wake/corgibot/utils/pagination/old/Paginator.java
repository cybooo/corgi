package cz.wake.corgibot.utils.pagination.old;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Scheduled for reallocation in 2.0
 *
 * <p>Full information on these and other 2.0 deprecations and changes can be found
 * <a href="https://gist.github.com/TheMonitorLizard/4f09ac2a3c9d8019dc3cde02cc456eee">here</a>
 *
 * @author John Grosh
 */
public class Paginator extends Menu {

    public static final String LEFT = "◀";
    public static final String STOP = "⏹";
    public static final String RIGHT = "▶";
    private final BiFunction<Integer, Integer, Color> color;
    private final BiFunction<Integer, Integer, String> text;
    private final int columns;
    private final int itemsPerPage;
    private final boolean showPageNumbers;
    private final boolean numberItems;
    private final List<String> strings;
    private final int pages;
    private final Consumer<Message> finalAction;
    private final boolean waitOnSinglePage;

    protected Paginator(EventWaiter waiter, Set<User> users, Set<Role> roles, long timeout, TimeUnit unit,
                        BiFunction<Integer, Integer, Color> color, BiFunction<Integer, Integer, String> text, Consumer<Message> finalAction,
                        int columns, int itemsPerPage, boolean showPageNumbers, boolean numberItems, List<String> items, boolean waitOnSinglePage) {
        super(waiter, users, roles, timeout, unit);
        this.color = color;
        this.text = text;
        this.columns = columns;
        this.itemsPerPage = itemsPerPage;
        this.showPageNumbers = showPageNumbers;
        this.numberItems = numberItems;
        this.strings = items;
        this.pages = (int) Math.ceil((double) strings.size() / itemsPerPage);
        this.finalAction = finalAction;
        this.waitOnSinglePage = waitOnSinglePage;
    }

    /**
     * Begins pagination on page 1 as a new {@link Message}
     * in the provided {@link MessageChannel}.
     *
     * @param channel The MessageChannel to send the new Message to
     */
    @Override
    public void display(MessageChannel channel) {
        paginate(channel, 1);
    }

    /**
     * Begins pagination on page 1 displaying this Pagination by editing the provided
     * {@link Message}.
     *
     * @param message The Message to display the Menu in
     */
    @Override
    public void display(Message message) {
        paginate(message, 1);
    }

    /**
     * Begins pagination as a new {@link Message}
     * in the provided {@link MessageChannel}, starting
     * on whatever page number is provided.
     *
     * @param channel The MessageChannel to send the new Message to
     * @param pageNum The page number to begin on
     */
    public void paginate(MessageChannel channel, int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        else if (pageNum > pages)
            pageNum = pages;
        Message msg = renderPage(pageNum);
        initialize(channel.sendMessage(msg), pageNum);
    }

    /**
     * Begins pagination displaying this Pagination by editing the provided
     * {@link Message}, starting on whatever
     * page number is provided.
     *
     * @param message The MessageChannel to send the new Message to
     * @param pageNum The page number to begin on
     */
    public void paginate(Message message, int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        else if (pageNum > pages)
            pageNum = pages;
        Message msg = renderPage(pageNum);
        initialize(message.editMessage(msg), pageNum);
    }

    private void initialize(RestAction<Message> action, int pageNum) {
        action.queue(m -> {
            if (pages > 1) {
                m.addReaction(Emoji.fromUnicode(LEFT)).queue();
                m.addReaction(Emoji.fromUnicode(STOP)).queue();
                m.addReaction(Emoji.fromUnicode(RIGHT)).queue(v -> pagination(m, pageNum), t -> pagination(m, pageNum));
            } else if (waitOnSinglePage) {
                m.addReaction(Emoji.fromUnicode(STOP)).queue(v -> pagination(m, pageNum), t -> pagination(m, pageNum));
            } else {
                finalAction.accept(m);
            }
        });
    }

    private void pagination(Message message, int pageNum) {
        waiter.waitForEvent(MessageReactionAddEvent.class, (MessageReactionAddEvent event) -> {
            if (!event.getMessageId().equals(message.getId()))
                return false;
            if (!(LEFT.equals(event.getEmoji().getName())
                    || STOP.equals(event.getEmoji().getName())
                    || RIGHT.equals(event.getEmoji().getName())))
                return false;
            return isValidUser(event.getUser(), event.getGuild());
        }, event -> {
            int newPageNum = pageNum;
            switch (event.getEmoji().getName()) {
                case LEFT:
                    if (newPageNum > 1) newPageNum--;
                    break;
                case RIGHT:
                    if (newPageNum < pages) newPageNum++;
                    break;
                case STOP:
                    finalAction.accept(message);
                    return;
            }
            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (PermissionException e) {
                e.printStackTrace();
            }
            int n = newPageNum;
            message.editMessage(renderPage(newPageNum)).queue(m -> pagination(m, n));
        }, timeout, unit, () -> finalAction.accept(message));
    }

    private Message renderPage(int pageNum) {
        MessageBuilder mbuilder = new MessageBuilder();
        EmbedBuilder ebuilder = new EmbedBuilder();
        int start = (pageNum - 1) * itemsPerPage;
        int end = Math.min(strings.size(), pageNum * itemsPerPage);
        if (columns == 1) {
            StringBuilder sbuilder = new StringBuilder();
            for (int i = start; i < end; i++)
                sbuilder.append("\n").append(numberItems ? "`" + (i + 1) + ".` " : "").append(strings.get(i));
            ebuilder.setDescription(sbuilder.toString());
        } else {
            int per = (int) Math.ceil((double) (end - start) / columns);
            for (int k = 0; k < columns; k++) {
                StringBuilder strbuilder = new StringBuilder();
                for (int i = start + k * per; i < end && i < start + (k + 1) * per; i++)
                    strbuilder.append("\n").append(numberItems ? (i + 1) + ". " : "").append(strings.get(i));
                ebuilder.addField("", strbuilder.toString(), true);
            }
        }

        ebuilder.setColor(color.apply(pageNum, pages));
        if (showPageNumbers)
            ebuilder.setFooter("Page " + pageNum + "/" + pages, null);
        mbuilder.setEmbeds(ebuilder.build());
        if (text != null)
            mbuilder.append(text.apply(pageNum, pages));
        return mbuilder.build();
    }
}
