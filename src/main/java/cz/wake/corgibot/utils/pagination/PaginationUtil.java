package cz.wake.corgibot.utils.pagination;

import cz.wake.corgibot.objects.ButtonGroup;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.buttons.ButtonUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtil {

    /**
     * Splits a string into a {@link PaginationList}
     *
     * @param content     The string to split
     * @param splitMethod the method by witch to split
     * @param splitAmount The amount to split
     * @return {@link PaginationList}
     */
    public static PaginationList<String> splitStringToList(String content, SplitMethod splitMethod, int splitAmount) {
        List<String> pages = new ArrayList<>();
        if (splitMethod == SplitMethod.CHAR_COUNT) {
            int pagesCount = Math.max((int) Math.ceil((double) content.length() / splitAmount), 1);
            String workingString = content;
            for (int i = 0; i < pagesCount; i++) {
                String substring = workingString.substring(0, Math.min(splitAmount, workingString.length()));
                int splitIndex = substring.lastIndexOf("\n") == -1 ? substring.length() : substring.lastIndexOf("\n");
                pages.add(substring.substring(0, splitIndex));
                if (i != (pagesCount - 1)) {
                    workingString = workingString.substring(splitIndex + 1);
                }
            }

        } else if (splitMethod == SplitMethod.NEW_LINES) {
            String[] lines = content.split("\n");
            int pagesCount = Math.max((int) Math.ceil((double) lines.length / splitAmount), 1);
            for (int i = 0; i < pagesCount; i++) {
                String[] page = ArrayUtils.subarray(lines, splitAmount * i, (splitAmount * i) + splitAmount);
                StringBuilder sb = new StringBuilder();
                for (String line : page) {
                    sb.append(line).append("\n");
                }
                pages.add(sb.toString());
            }
        }
        return new PaginationList<>(pages);
    }

    /**
     * Sends a paged message
     *
     * @param textChannel The channel to send it to
     * @param list        The {@link PaginationList} to use
     * @param page        The starting page
     * @param sender      The member who requested the button
     */
    public static void sendPagedMessage(MessageChannel textChannel, PaginationList list, int page, User sender, String group) {
        if (page < 0 || page > list.getPages() - 1) {
            MessageUtils.sendErrorMessage("Invalid page: " + (page + 1) + " Total Pages: " + list.getPages(), textChannel);
            return;
        }
        Integer[] pages = new Integer[]{page};
        if (list.getPages() > 1) {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), group);
            buttonGroup.addButton(new ButtonGroup.Button("\u23EE", (ownerID, user, message) -> {
                //Start
                pages[0] = 0;
                message.editMessage(list.getPage(pages[0])).queue();
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23EA", (ownerID, user, message) -> {
                //Prev
                if (pages[0] != 0) {
                    pages[0] -= 1;
                    message.editMessage(list.getPage(pages[0])).queue();
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23E9", (ownerID, user, message) -> {
                //Next
                if (pages[0] + 1 != list.getPages()) {
                    pages[0] += 1;
                    message.editMessage(list.getPage(pages[0])).queue();
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23ED", (ownerID, user, message) -> {
                //Last
                pages[0] = list.getPages() - 1;
                message.editMessage(list.getPage(pages[0])).queue();
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u274C", (ownerID, user, message) -> {
                // Delete
                if (user.getIdLong() == ownerID ||
                        message.getGuild().getMember(user).hasPermission(Permission.MANAGE_PERMISSIONS)) {
                    message.delete().queue(null, e -> {
                    });
                } else {
                    MessageUtils.sendErrorMessage("You need to be the sender or have the `Manage Messages` discord permission to do this!", message.getChannel());
                }
            }));
            ButtonUtil.sendButtonedMessage(textChannel, list.getPage(page), buttonGroup);
        } else {
            textChannel.sendMessage(list.getPage(page)).queue();
        }
    }

    /**
     * Sends an embed paged message the to specified channel.
     * You can build with Embed.
     *
     * @param pagedEmbed The to use.
     * @param page       The page to start on (0 Indexed).
     * @param channel    The channel to send the paged message to.
     * @param sender     The user who requested the embed
     */
    public static void sendEmbedPagedMessage(PagedEmbedBuilder.PagedEmbed pagedEmbed, int page, TextChannel channel, User sender, String group) {
        if (page < 0 || page > pagedEmbed.getPageTotal() - 1) {
            MessageUtils.sendErrorMessage("Invalid page: " + (page + 1) + " Total Pages: " + pagedEmbed.getPageTotal(), channel);
            return;
        }
        if (!pagedEmbed.isSinglePage()) {
            ButtonGroup buttonGroup = new ButtonGroup(sender.getIdLong(), group);
            Integer[] pages = new Integer[]{page};
            buttonGroup.addButton(new ButtonGroup.Button("\u23EE", (ownerID, user, message) -> {
                //Start
                pages[0] = 0;
                message.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23EA", (ownerID, user, message) -> {
                //Prev
                if (pages[0] != 0) {
                    pages[0] -= 1;
                    message.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23E9", (ownerID, user, message) -> {
                //Next
                if (pages[0] + 1 != pagedEmbed.getPageTotal()) {
                    pages[0] += 1;
                    message.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
                }
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u23ED", (ownerID, user, message) -> {
                //Last
                pages[0] = pagedEmbed.getPageTotal() - 1;
                message.editMessageEmbeds(pagedEmbed.getEmbed(pages[0])).queue();
            }));
            buttonGroup.addButton(new ButtonGroup.Button("\u274C", (ownerID, user, message) -> {
                // Delete
                if (user.getIdLong() == ownerID ||
                        message.getGuild().getMember(user).hasPermission(Permission.MANAGE_PERMISSIONS)) {
                    message.delete().queue(null, e -> {
                    });
                } else {
                    MessageUtils.sendErrorMessage("You need to be the sender or have the `Manage Messages` discord permission to do this!", message.getChannel());
                }
            }));
            ButtonUtil.sendButtonedMessage(channel, pagedEmbed.getEmbed(page), buttonGroup);
        } else {
            channel.sendMessageEmbeds(pagedEmbed.getEmbed(page)).queue();
        }
    }

    /**
     * This is a sub-enum used to determine how the content will be split and displayed in pages.
     */
    public enum SplitMethod {
        CHAR_COUNT,
        NEW_LINES
    }
}
