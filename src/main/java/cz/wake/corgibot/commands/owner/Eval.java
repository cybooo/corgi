package cz.wake.corgibot.commands.owner;

import cz.wake.corgibot.CorgiBot;
import cz.wake.corgibot.commands.CommandType;
import cz.wake.corgibot.commands.ICommand;
import cz.wake.corgibot.commands.Rank;
import cz.wake.corgibot.utils.MessageUtils;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Eval implements ICommand {

    private ScriptEngineManager manager = new ScriptEngineManager();
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

    @Override
    public void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member, EventWaiter w, String guildPrefix) {
        String imports =
                IMPORTS.stream().map(s -> "Packages." + s).collect(Collectors.joining(", ", "var imports = new JavaImporter(", ");\n"));
        ScriptEngine engine = manager.getEngineByName("nashorn");
        engine.put("channel", channel);
        engine.put("guild", member.getGuild());
        engine.put("message", message);
        engine.put("jda", sender.getJDA());
        engine.put("sender", sender);
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
                    channel.sendMessage(MessageUtils.getEmbed(sender)
                            .addField("Code:", "```js\n" + code + "```", false)
                            .addField("Result: ", eResult, false).build()).queue();
            } catch (Exception e) {
                CorgiBot.LOGGER.error("Error occured in the evaluator thread pool!", e);
                channel.sendMessage(MessageUtils.getEmbed(sender)
                        .addField("Code:", "```js\n" + code + "```", false)
                        .addField("Result: ", "```bf\n" + e.getMessage() + "```", false).build()).queue();
            }
        });
    }

    @Override
    public String getCommand() {
        return "eval";
    }

    @Override
    public String getDescription() {
        return "Prostě eval";
    }

    @Override
    public String getHelp() {
        return ".eval";
    }

    @Override
    public CommandType getType() {
        return CommandType.BOT_OWNER;
    }

    @Override
    public Rank getRank() {
        return Rank.BOT_OWNER;
    }
}
