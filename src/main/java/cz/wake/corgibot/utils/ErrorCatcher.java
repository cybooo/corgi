package cz.wake.corgibot.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import cz.wake.corgibot.CorgiBot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ErrorCatcher extends Filter<ILoggingEvent> {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @Override
    public FilterReply decide(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        if (msg == null)
            msg = "null";
        if (event.getMarker() != Markers.NO_ANNOUNCE
                && CorgiBot.getInstance() != null
                //&& CorgiBot.getInstance().isReady()
                && event.getLevel() == Level.ERROR) {
            String finalMsg = msg;
            if (event.getThreadName().startsWith("lava-daemon-pool")) {
                return FilterReply.NEUTRAL;
            }
            EXECUTOR.submit(() -> {
                Throwable throwable = null;
                if (event.getThrowableProxy() != null && event.getThrowableProxy() instanceof ThrowableProxy) {
                    throwable = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();
                }
                if (throwable != null) {
                    //MessageUtils.sendException(finalMsg, throwable, FlareBot.getInstance().getUpdateChannel());
                } //else CorgiBot.getInstance().getUpdateChannel().sendMessage(finalMsg).queue();
            });
        }
        return FilterReply.NEUTRAL;
    }
}
