package cz.wake.corgibot.metrics;

import cz.wake.corgibot.metrics.collectors.BotCollector;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Metrics {

    private static final Logger logger = LoggerFactory.getLogger(Metrics.class);

    private static Metrics instance;

    private final HTTPServer server;

    public static void setup() {
        logger.info("Setup metrics {}!", instance().toString());
    }

    public static Metrics instance() {
        if (instance == null)
            instance = new Metrics();
        return instance;
    }

    public final JdaEventMetricsListener jdaEventMetricsListener = new JdaEventMetricsListener();

    public Metrics() {
        DefaultExports.initialize();

        new BotCollector(new BotMetrics()).register();

        try {
            server = new HTTPServer(9191);
            logger.info("Setup HTTPServer for Metrics");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to set up HTTPServer for Metrics", e);
        }
    }

    public HTTPServer getServer() {
        return server;
    }

    // Conventions:
    // Names: https://prometheus.io/docs/practices/naming/
    // Labels: https://prometheus.io/docs/practices/instrumentation/#use-labels

    /*
     * JDA
     */
    //TODO: Ping
    public static final Counter jdaEvents = Counter.build()
            .name("corgibot_jda_events_total")
            .help("Total number of JDA events fired")
            .labelNames("class") // Use the simple name of the event class eg GuildMessageReceivedEvent, DisconnectEvent
            .register();

    public static final Counter websocketEvents = Counter.build()
            .name("corgibot_websocket_events_total")
            .help("Total number of WebSocket events fired")
            .labelNames("op_code", "event_name", "type")
            .register();

    public static final Counter failedRestActions = Counter.build()
            .name("corgibot_rest_actions_total")
            .help("Total number of failed restactions executed by Corgibot")
            .labelNames("error_response_code") // Use the error response code eg 50001, 50007 etc
            .register();

    /*
     * CorgiBot
     */
    public static final Counter blocksGivenOut = Counter.build()
            .name("corgibot_guild_blocked_total")
            .help("Total number of times we've blocked guilds")
            .labelNames("guild_id")
            .register();

    public static final Counter totalGiveaways = Counter.build()
            .name("corgibot_guild_giveaways_total")
            .help("Total number of ran giveaways")
            .labelNames("guild_name")
            .register();

    public static final Counter commandsReceived = Counter.build()
            .name("corgibot_commands_received_total")
            .help("Total amount of commands ran by users")
            .labelNames("class")
            .register();

    public static final Counter commandsExecuted = Counter.build()
            .name("corgibot_commands_executed_total")
            .help("Total amount of commands that we executed")
            .labelNames("class")
            .register();

    public static final Histogram commandExecutionTime = Histogram.build()
            .name("corgibot_command_execution_duration_seconds")
            .help("Command execution time in seconds")
            .labelNames("class")
            .register();

    public static final Counter commandExceptions = Counter.build()
            .name("corgibot_command_exceptions_total")
            .help("Total uncaught exceptions thrown by the command")
            .labelNames("class")
            .register();

    /*
     * HTTP
     */
    public static Counter httpRequestCounter = Counter.build()
            .name("corgibot_okhttp_requests_total")
            .help("Total OkHttp requests made and the requester")
            .labelNames("request_sender")
            .register();

}
