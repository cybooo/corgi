package cz.wake.corgibot.commands.user;

import com.jagrosh.jdautilities.waiter.EventWaiter;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.objects.GuildWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;

@SinceCorgi(version = "0.9")
public class FullWidth implements ICommand {

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        if (args.length > 1) {
            String rawSplit[] = message.getContentRaw().split(" ", 2);
            if (rawSplit.length < 2) {
                channel.sendMessage("Ｍｕｓｉ　ｔｏ　ｂｙｔ　ｄｅｌｓｉ！").queue();
            } else {
                channel.sendMessage(StringUtils.replaceEach(rawSplit[1], toReplace, replacements)).queue();
            }
        }
    }

    @Override
    public String getCommand() {
        return "fullwidth";
    }

    @Override
    public String getDescription() {
        return "Vytvoř text, který je ＦＵＬＬＷＩＤＴＨ";
    }

    @Override
    public String getHelp() {
        return "%fullwidth <text>";
    }

    @Override
    public CommandType getType() {
        return CommandType.FUN;
    }

    @Override
    public Rank getRank() {
        return Rank.USER;
    }

    private static String[] toReplace = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
            "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
            "4", "5", "6", "7", "8", "9", "0", "-", "=", "\\", "]", "[", "{", "}", "/", ",", ".", "!", "?",
            "@", "#", "$", "%", "^", "&", "*", "(", ")", "~", "`", "\"", "'", " "};
    private static String[] replacements = {"ａ", "ｂ", "ｃ", "ｄ", "ｅ", "ｆ", "ｇ", "ｈ", "ｉ", "ｊ", "ｋ", "ｌ", "ｍ", "ｎ",
            "ｏ", "ｐ", "ｑ", "ｒ", "ｓ", "ｔ", "ｕ", "ｖ", "ｗ", "ｘ", "ｙ", "ｚ", "Ａ", "Ｂ", "Ｃ", "Ｄ", "Ｅ",
            "Ｆ", "Ｇ", "Ｈ", "Ｉ", "Ｊ", "Ｋ", "Ｌ", "Ｍ", "Ｎ", "Ｏ", "Ｐ", "Ｑ", "Ｒ", "Ｓ", "Ｔ", "Ｕ", "Ｖ",
            "Ｗ", "Ｘ", "Ｙ", "Ｚ", "１", "２", "３", "４", "５", "６", "７", "８", "９", "０", "－", "＝", "＼",
            "]", "[", "｛", "｝", "／", ",", "．", "！", "？", "＠", "＃", "＄", "％", "＾", "＆", "＊", "（", "）", "~", "`", "”", "’", "　"};
}
