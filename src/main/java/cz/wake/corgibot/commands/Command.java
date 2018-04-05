package cz.wake.corgibot.commands;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("SameParameterValue")
public interface Command {

    /**
     * Excecuted when command was relized by {@link CommandHandler}
     * @param channel {@link MessageChannel} where message was sended.
     * @param message Complete message with prefix.
     * @param args Arguments from message in array.
     * @param member {@link Member] who executed a command.
     * @param w {@link EventWaiter} for furure actions.
     * @param gw {@link GuildWrapper} for getting settings etc.
     */
    void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw);

    /**
     * Name of the command.
     * @return name
     */
    String getCommand();

    /**
     * Basic description of a command.
     * @return description
     */
    String getDescription();

    /**
     * Advanced help for command <bold>c!help <command></bold>
     * @return help
     */
    String getHelp();

    /**
     * Category where command will be displayed in help menu.
     * @return {@link CommandCategory}
     */
    CommandCategory getCategory();

    default String[] getAliases() {
        return new String[]{};
    }

    default boolean deleteMessage() {
        return false;
    }

    default boolean isBeta() {
        return false;
    }

    default boolean isOwner(){
        return false;
    }

    default int specificCooldown(){
        return -1;
    }

    default Permission[] userPermission(){
        return new Permission[]{};
    }

    default Permission[] botPermission(){
        return new Permission[]{};
    }


}
