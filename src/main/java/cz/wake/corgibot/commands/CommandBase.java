package cz.wake.corgibot.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

@SuppressWarnings("SameParameterValue")
public interface CommandBase {

    /**
     * Excecuted when command was relized by {@link CommandManager}
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
     * If true Corgi will delete original message from user.
     *
     * @return {@link Boolean}
     */
    default boolean deleteMessage() {
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
     * Name of the command.
     *
     * @return name
     */
    default String getCommand(){
        return this.getClass().getAnnotation(CommandInfo.class).name();
    }

    /**
     * Basic description of a command.
     *
     * @return description
     */
    default String getDescription(){
        return this.getClass().getAnnotation(CommandInfo.class).description();
    }

    /**
     * Advanced help for command <bold>c!help <command></bold>
     *
     * @return help
     */
    default String getHelp(){
        return this.getClass().getAnnotation(CommandInfo.class).help();
    }
}
