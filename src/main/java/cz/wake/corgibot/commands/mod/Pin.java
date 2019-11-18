package cz.wake.corgibot.commands.mod;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.commands.Command;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Pin implements Command {

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length == 1 && args[0].matches("[0-9]{18,22}")) {

            Message msg = channel.getMessageById(args[0].trim()).complete();
            if (msg == null) {
                MessageUtils.sendErrorMessage("Požadovaná zpráva nebyla nalezena!", channel);
                return;
            }
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else if (args.length != 0) {
            Message msg = channel.sendMessage(new EmbedBuilder().setTitle(member.getUser().getName(), null)
                    .setThumbnail(MessageUtils.getAvatar(member.getUser())).setDescription(MessageUtils.getMessage(args, 0))
                    .build()).complete();
            msg.pin().complete();
            channel.getHistory().retrievePast(1).complete().get(0).delete().queue();
        } else {
            channel.sendMessage(MessageUtils.getEmbed(Constants.GRAY).setTitle("Nápověda k příkazu %ping".replace("%", gw.getPrefix()))
                .setDescription(getDescription()).build()).queue();
        }
    }

    @Override
    public String getCommand() {
        return "pin";
    }

    @Override
    public String getDescription() {
        return "Příkaz, se kterým lze připnout zprávu nebo vygenerovat zprávu k připnutí.";
    }

    @Override
    public String getHelp() {
        return "%pin <ID|zpráva>` - Připne zprávu podle ID nebo vygeneruje novou a připne.";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pripnout"};
    }

    @Override
    public boolean deleteMessage() {
        return true;
    }

    @Override
    public Permission[] userPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE};
    }

    @Override
    public Permission[] botPermission() {
        return new Permission[]{Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE};
    }
}
