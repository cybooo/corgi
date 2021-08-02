package cz.wake.corgibot.commands.admin;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

@CommandInfo(
        name = "ticket",
        description = "Manage tickets",
        help = "%ticket",
        category = CommandCategory.ADMINISTRATOR,
        userPerms = {Permission.MANAGE_CHANNEL}

)
@SinceCorgi(version = "1.3.6")
public class Ticket implements CommandBase {

    private void sendHelp(MessageChannel channel, GuildWrapper gw) {
        channel.sendMessage(
                """
                        **Commands**
                        %ticket liststaffroles
                        %ticket addstaffrole <roleid>
                        %ticket removestaffrole <roleid>
                        %ticket setmaximumtickets <number>
                        %ticket setmaximumusertickets <number>
                        %ticket setopenedcategory <categoryid>
                        %ticket setclosedcategory <categoryid>
                        %ticket settranscriptchannel <channelid>
                        """.replace("%", gw.getPrefix())).queue();
    }

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {

        if (!CorgiBot.getInstance().getSql().registeredTicketData(gw.getGuildId())) {
            CorgiBot.getInstance().getSql().registerTicketData(gw.getGuildId());
        }

        if (args.length == 0) {
            sendHelp(channel, gw);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("liststaffroles")) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                StringBuilder stringBuilder = new StringBuilder();
                embedBuilder.setColor(Constants.BLUE);
                embedBuilder.setTitle("Roles that can access tickets");
                for (Role role : CorgiBot.getInstance().getSql().getTicketStaffRoles(gw.getGuildId())) {
                    stringBuilder.append("- **").append(role.getName()).append("**");
                }
                embedBuilder.setDescription(stringBuilder);
                channel.sendMessageEmbeds(embedBuilder.build()).queue();
            } else {
                sendHelp(channel, gw);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("addstaffrole")) {
                try {
                    if (message.getGuild().getRoleById(args[1]) == null) {
                        MessageUtils.sendErrorMessage("Role with this id was not found!", channel);
                        return;
                    }
                } catch (NumberFormatException e) {
                    MessageUtils.sendErrorMessage("Role with this id was not found!", channel);
                    return;
                }
                CorgiBot.getInstance().getSql().addTicketStaffRole(gw.getGuildId(), args[1]);
                channel.sendMessage("Role added!").queue();
            } else if (args[0].equalsIgnoreCase("removestaffrole")) {
                CorgiBot.getInstance().getSql().deleteTicketStaffRole(gw.getGuildId(), args[1]);
                channel.sendMessage("Role removed!").queue();
            } else if (args[0].equalsIgnoreCase("setmaximumtickets")) {
                try {
                    int maximumTickets = Integer.parseInt(args[1]);
                    channel.sendMessage("Maximum tickets set to **" + maximumTickets + "**!").queue();
                    CorgiBot.getInstance().getSql().setMaximumTickets(gw.getGuildId(), maximumTickets);
                } catch (NumberFormatException e) {
                    MessageUtils.sendErrorMessage("Value needs to be a number!", channel);
                }
            } else if (args[0].equalsIgnoreCase("setmaximumusertickets")) {
                try {
                    int maximumUserTickets = Integer.parseInt(args[1]);
                    channel.sendMessage("Maximum tickets per user set to **" + maximumUserTickets + "**!").queue();
                    CorgiBot.getInstance().getSql().setMaximumUserTickets(gw.getGuildId(), maximumUserTickets);
                } catch (NumberFormatException e) {
                    MessageUtils.sendErrorMessage("Value needs to be a number!", channel);
                }
            } else if (args[0].equalsIgnoreCase("setopenedcategory")) {
                try {
                    Integer.parseInt(args[1]);
                    channel.sendMessage("Opened category set to **" + args[1] + "**!").queue();
                    CorgiBot.getInstance().getSql().setTicketOpenedCategory(gw.getGuildId(), args[1]);
                } catch (NumberFormatException e) {
                    MessageUtils.sendErrorMessage("Value needs to be a number!", channel);
                }
            } else if (args[0].equalsIgnoreCase("setclosedcategory")) {
            } else if (args[0].equalsIgnoreCase("settranscriptchannel")) {


            } else {
                sendHelp(channel, gw);
            }
        }
    }


}
