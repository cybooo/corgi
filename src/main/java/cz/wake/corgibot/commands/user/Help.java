package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.commands.CommandManager;
import cz.wake.corgibot.commands.FinalCommand;
import cz.wake.corgibot.managers.BotManager;
import cz.wake.corgibot.objects.guild.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import cz.wake.corgibot.utils.lang.I18n;
import net.dv8tion.jda.api.entities.*;

@CommandInfo(
        name = "help",
        aliases = {"commands"},
        description = "commands.help.description",
        help = "commands.help.help",
        category = CommandCategory.GENERAL
)
@SinceCorgi(version = "0.1")
public class Help implements CommandBase {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length < 1) {
            if (channel.getType() == ChannelType.TEXT) {
                channel.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE).setTitle(I18n.getLoc(gw, "commands.help.check-your-messages"))
                        .setDescription(EmoteList.MAILBOX + " | " + I18n.getLoc(gw, "commands.help.sent-help")).build()).queue();
            }
            member.getUser().openPrivateChannel().queue(msg -> msg.sendMessageEmbeds(MessageUtils.getEmbed(Constants.BLUE)
                    .setAuthor(I18n.getLoc(gw, "commands.help.commands"), null, channel.getJDA().getSelfUser().getAvatarUrl())
                    .setDescription(getContext(member, message.getGuild())).setFooter(I18n.getLoc(gw, "commands.help.find-all"), null)
                    .build()).queue());
        } else {
            String commandName = args[0];
            CommandManager cm = CorgiBot.getInstance().getCommandManager();
            //Normal
            cm.getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(commandName)).forEach(c -> {
                String description = c.getDescription().startsWith("commands") ? I18n.getLoc(gw, c.getDescription()) : c.getDescription();
                String help = c.getHelp().startsWith("commands") ? I18n.getLoc(gw, c.getHelp()) : c.getHelp();
                channel.sendMessageEmbeds(MessageUtils.getEmbed().setTitle(String.format(I18n.getLoc(gw, "commands.help.help-for-command"), commandName))
                        .setDescription(description + "\n\n**" + I18n.getLoc(gw, "internal.general.usage") + "**\n" + help.replace("%", gw.getPrefix()))
                        .setColor(Constants.BLUE).setFooter(I18n.getLoc(gw, "commands.help.aliases") + String.join(", ", c.getAliases()), null).build()).queue();
            });
        }
    }

    private StringBuilder getContext(Member member, Guild guild) {
        StringBuilder builder = new StringBuilder();
        CommandManager cm = CorgiBot.getInstance().getCommandManager();
        GuildWrapper gw = BotManager.getCustomGuild(guild.getId());
        try {
            builder.append(String.format(I18n.getLoc(gw, "commands.help.prefix-for-guild"), guild.getName(), gw.getPrefix(), gw.getPrefix()));
        } catch (NullPointerException ex) {
            builder.append(I18n.getLoc(gw, "commands.help.prefix-invalid-guild"));
        }
        for (CommandCategory type : CommandCategory.getTypes()) {
            if (type == CommandCategory.BOT_OWNER || type == CommandCategory.HIDDEN) {
                return builder;
            }
            builder.append("\n\n");
            builder.append(type.getEmote()).append(" | **").append(I18n.getLoc(gw, "commands.categories." + type.toString().toLowerCase())).append("** - ").append(cm.getCommandsByCategory(type).size()).append("\n");
            for (FinalCommand c : cm.getCommands()) {
                if (c.getCommandCategory().equals(type)) {
                    builder.append("`").append(c.getName()).append("` ");
                }
            }
        }
        return builder;
    }

}
