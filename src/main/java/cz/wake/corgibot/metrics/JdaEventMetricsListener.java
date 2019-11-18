package cz.wake.corgibot.metrics;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

/**
 * This class is used in extension of the Metrics class, this will collect all the JDA events used and allow us to see
 * which fires are firing the most.
 */
public class JdaEventMetricsListener implements EventListener {

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        Metrics.jdaEvents.labels(event.getClass().getSimpleName()).inc();
    }
}
