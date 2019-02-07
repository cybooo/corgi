package cz.wake.corgibot.metrics.collectors;

import cz.wake.corgibot.metrics.BotMetrics;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BotCollector extends Collector {

    private final BotMetrics botMetrics;

    public BotCollector(BotMetrics botMetrics) {
        this.botMetrics = botMetrics;
    }

    @Override
    public List<MetricFamilySamples> collect() {

        List<MetricFamilySamples> familySamples = new ArrayList<>();

        GaugeMetricFamily jdaEntities = new GaugeMetricFamily("corgibot_jda_entities_total", "Amount of JDA entities",
                Collections.singletonList("entity"));
        familySamples.add(jdaEntities);

        GaugeMetricFamily playerInfo = new GaugeMetricFamily("corgibot_player_info",
                "Amount of players, playing players and songs queued", Collections.singletonList("amount"));
        familySamples.add(playerInfo);

        if (botMetrics.count()) {
            jdaEntities.addMetric(Collections.singletonList("guilds"), botMetrics.getGuildCount());
            jdaEntities.addMetric(Collections.singletonList("users"), botMetrics.getUserCount());
            jdaEntities.addMetric(Collections.singletonList("text_channels"), botMetrics.getTextChannelCount());
            jdaEntities.addMetric(Collections.singletonList("voice_channels"), botMetrics.getVoiceChannelCount());

            //playerInfo.addMetric(Collections.singletonList("connected_voice_channels"), Getters.getConnectedVoiceChannels());
            //playerInfo.addMetric(Collections.singletonList("active_voice_channels"), Getters.getActiveVoiceChannels());
            //playerInfo.addMetric(Collections.singletonList("songs_queued"), Getters.getSongsQueued());
        }

        return familySamples;
    }
}
