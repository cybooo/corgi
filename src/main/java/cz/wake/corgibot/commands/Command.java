package cz.wake.corgibot.commands;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

@SuppressWarnings("SameParameterValue")
public interface Command {

    /**
     * Excecuted when command was relized by {@link CommandHandler}
     *
     * @param channel {@link MessageChannel} where message was sended.
     * @param message Complete message with prefix.
     * @param args    Arguments from message in array.
     * @param member  {@link Member] who executed a command.
     * @param w       {@link EventWaiter} for furure actions.
     * @param gw      {@link GuildWrapper} for getting settings etc.
     */
    void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw);

    /**
     * Name of the command.
     *
     * @return name
     */
    String getCommand();

    /**
     * Basic description of a command.
     *
     * @return description
     */
    String getDescription();

    /**
     * Advanced help for command <bold>c!help <command></bold>
     *
     * @return help
     */
    String getHelp();

    /**
     * Category where command will be displayed in help menu.
     *
     * @return {@link CommandCategory}
     */
    CommandCategory getCategory();

    /**
     * Array of aliases for commands.
     *
     * @return {@link java.lang.reflect.Array}
     */
    default String[] getAliases() {
        return new String[]{};
    }

    /**
     * If true Corgi will delete original message from user.
     *
     * @return {@link Boolean}
     */
    default boolean deleteMessage() {
        return false;
    }

    /**
     * If is this command in beta stage (not public)
     *
     * @return {@link Boolean}
     */
    default boolean isBeta() {
        return false;
    }

    /**
     * Command that can use only owner (MrWakeCZ#0001)
     *
     * @return {@link Boolean}
     */
    default boolean isOwner() {
        return false;
    }

    /**
     * If has command specific cooldown other than normal.
     *
     * @return count of cooldown, -1 if is used default (counted by users in guild)
     */
    default int specificCooldown() {
        return -1;
    }

    /**
     * Array of permissions that sender must have for use this command.
     *
     * @return {@link java.lang.reflect.Array} of {@link Permission}
     */
    default Permission[] userPermission() {
        return new Permission[]{};
    }

    /**
     * Array of permission that mush have Corgi
     *
     * @return {@link java.lang.reflect.Array} of {@link Permission}
     */
    default Permission[] botPermission() {
        return new Permission[]{};
    }


}
