package cz.wake.corgibot.commands.user;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.CommandUse;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.Constants;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class Changelog implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w) {
        if(args.length < 1){
            List<Integer> ids = CorgiBot.getInstance().getSql().getChangelogRow();
            List<String> changes = CorgiBot.getInstance().getSql().getChangelogText();
            EmbedBuilder builder = MessageUtils.getEmbed(Constants.GRAY).setTitle("Seznam posledních změn :pencil:");
            for(int i = 0; i < changes.size(); i++){

                String request = StringUtils.join(changes.get(i), " ");
                String[] arguments = request.split("\\|");

                StringBuilder b = new StringBuilder();
                for(String s : arguments){
                    b.append(s.trim() + "\n");
                }

                builder.addField("1.1." + ids.get(i), b.toString(), false);
            }
            channel.sendMessage(builder.build()).queue();
        } else {
            if(args[0].equalsIgnoreCase("add")){
                if(sender.getId().equals("177516608778928129") && member.isOwner()){
                    try {
                        String messageEdited = message.getContent().replace(".changelog add ", "");
                        CorgiBot.getInstance().getSql().insertChnge(messageEdited);
                        channel.sendMessage(MessageUtils.getEmbed(Constants.GREEN).setDescription("Do changelogu přidána položka: **" + messageEdited + "**").build()).queue();
                    } catch (Exception e){
                        CorgiBot.LOGGER.error("Chyba při provádění příkazu .changelog add!", e);
                    }
                }
            }
        }
    }

    @Override
    public String getCommand() {
        return "changelog";
    }

    @Override
    public String getDescription() {
        return "Získej přehled o posledních změnách";
    }

    @Override
    public String getHelp() {
        return ".changelog";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    @Override
    public CommandUse getUse() {
        return CommandUse.ALL;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }
}
