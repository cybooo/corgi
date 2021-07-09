package cz.wake.corgibot.commands.owner;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.annotations.CommandInfo;
import cz.wake.corgibot.annotations.OnlyOwner;
import cz.wake.corgibot.annotations.SinceCorgi;
import cz.wake.corgibot.commands.CommandBase;
import cz.wake.corgibot.commands.CommandCategory;
import cz.wake.corgibot.objects.GuildWrapper;
import cz.wake.corgibot.utils.MessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@OnlyOwner
@CommandInfo(
        name = "eval",
        description = "Eval command",
        help = "%eval",
        category = CommandCategory.BOT_OWNER
)
@SinceCorgi(version = "1.2.2")
public class Eval implements CommandBase {

    private static final ThreadGroup EVALS = new ThreadGroup("EvalCommand Thread Pool");
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> new Thread(EVALS, r,
            EVALS.getName() + EVALS.activeCount()));
    private static final List<String> IMPORTS = Arrays.asList(
            "cz.wake.corgibot",
            "cz.wake.corgibot.utils",
            "cz.wake.corgibot.sql",
            "cz.wake.corgibot.scheluder",
            "cz.wake.corgibot.runnable",
            "cz.wake.corgibot.managers",
            "cz.wake.corgibot.listener",
            "cz.wake.corgibot.commands",
            "net.dv8tion.jda.core",
            "net.dv8tion.jda.core.managers",
            "net.dv8tion.jda.core.entities.impl",
            "net.dv8tion.jda.core.entities",
            "java.util.streams",
            "java.util",
            "java.lang",
            "java.text",
            "java.lang",
            "java.math",
            "java.time",
            "java.io",
            "java.nio",
            "java.nio.files",
            "java.util.stream");
    private final ScriptEngineManager manager = new ScriptEngineManager();

    @Override
    public void onCommand(MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, GuildWrapper gw) {
        String imports =
                IMPORTS.stream().map(s -> "Packages." + s).collect(Collectors.joining(", ", "var imports = new JavaImporter(", ");\n"));
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.put("channel", channel);
        engine.put("guild", member.getGuild());
        engine.put("message", message);
        engine.put("jda", member.getUser().getJDA());
        engine.put("sender", member.getUser());
        String code;
        boolean silent = args.length > 0 && args[0].equalsIgnoreCase("-s");
        if (silent)
            code = MessageUtils.getMessage(args, 1);
        else
            code = Arrays.stream(args).collect(Collectors.joining(" "));
        POOL.submit(() -> {
            try {
                String eResult = String.valueOf(engine.eval(imports + "with (imports) {\n" + code + "\n}"));
                if (("```js\n" + eResult + "\n```").length() > 1048) {
                    eResult = String.format("[Result](%s)", MessageUtils.hastebin(eResult));
                } else eResult = "```js\n" + eResult + "\n```";
                if (!silent)
                    channel.sendMessage(MessageUtils.getEmbed(member.getUser())
                            .addField("Code:", "```js\n" + code + "```", false)
                            .addField("Result: ", eResult, false).build()).queue();
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Error occured in the evaluator thread pool!", e);
                channel.sendMessage(MessageUtils.getEmbed(member.getUser())
                        .addField("Code:", "```js\n" + code + "```", false)
                        .addField("Result: ", "```bf\n" + e.getMessage() + "```", false).build()).queue();
            }
        });
    }

}
