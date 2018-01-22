package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.EmoteList;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Random;

@SinceCorgi(version = "3.3.0")
public class Choose implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if(args.length < 1){
            MessageUtils.sendErrorMessage("Musíš si něco vybrat!", channel);
        } else {
            // Format message
            String request = message.getRawContent().replaceAll("\\s+","").replace(".choose","");
            System.out.println("Request: " + request); // text|text2|text3
            String[] arguments = request.split("\\|");

            if(arguments.length == 1){
                MessageUtils.sendErrorMessage("Musíš zadat víc než 1 volbu!", channel);
                return;
            }

            channel.sendMessage(getRandomThinkingEmote() + " | **" + sender.getName() + "**, zvolil jsem **" + arguments[(int)(Math.random()*arguments.length)] + "**!").queue();
        }
    }

    @Override
    public String getCommand() {
        return "choose";
    }

    @Override
    public String getDescription() {
        return "Nevíš co? Nech Corgiho ať rozhodne za tebe.";
    }

    @Override
    public String getHelp() {
        return "%choose volba1 | volba2 | volba3";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"volba"};
    }

    private String getRandomThinkingEmote(){
        Random r = new Random();
        int number = r.nextInt(3) + 1;
        switch (number){
            case 1:
                return EmoteList.THINKING_1;
            case 2:
                return EmoteList.THINKING_2;
            case 3:
                return EmoteList.THINKING_3;
            default:
                return EmoteList.THINKING_1;
        }
    }
}
