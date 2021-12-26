package carpet.prometheus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import carpet.prometheus.metrics.AbstractMetric;
import carpet.prometheus.metrics.EntitiesMetrics;
import carpet.prometheus.metrics.LoadedChunksMetrics;
import carpet.prometheus.metrics.MSPTMetric;
import carpet.prometheus.metrics.OnlinePlayersMetrics;
import carpet.prometheus.metrics.RamMetric;
import carpet.prometheus.metrics.TPSMetric;
import carpet.prometheus.metrics.TileEntitiesMetrics;

public class MetricReg {
	
	private final PrometheusExtension extension;
    private Timer timer;
    private final List<AbstractMetric> metrics = new ArrayList<>();
    
    public MetricReg(PrometheusExtension prometheusExtension) {
        this.extension = prometheusExtension;
    }

	public void registerMetrics() {
		MSPTMetric msptMetric = new MSPTMetric("mspt", "Current MSPT on server.");
        msptMetric.getGauge().register();
        
        TPSMetric tpsMetric = new TPSMetric("tps", "Current TPS on server.");
        tpsMetric.getGauge().register();

        LoadedChunksMetrics loadedChunksMetrics = new LoadedChunksMetrics("loaded_chunks", "Amount of loaded chunks.");
        loadedChunksMetrics.getGauge().register();
        
        EntitiesMetrics entitiesMetrics = new EntitiesMetrics("entities", "Amount of entities.");
        entitiesMetrics.getGauge().register();
        
        TileEntitiesMetrics blockEntitiesMetrics = new TileEntitiesMetrics("tile_entities", "Amount of tile entities");
        blockEntitiesMetrics.getGauge().register();
        
        RamMetric ramMetric = new RamMetric("ram", "Usage of RAM.");
        ramMetric.getGauge().register();
        
        OnlinePlayersMetrics onlinePlayersMetrics = new OnlinePlayersMetrics("online_players", "Connected players");
        onlinePlayersMetrics.getGauge().register();
        
        metrics.add(msptMetric);
        metrics.add(tpsMetric);
        metrics.add(loadedChunksMetrics);
        metrics.add(entitiesMetrics);
        metrics.add(blockEntitiesMetrics);
        metrics.add(ramMetric);
        metrics.add(onlinePlayersMetrics);
	}

	public void runUpdater() {
		if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer();
        MetricUpdater metricUpdater = new MetricUpdater(this.extension);
        for (AbstractMetric m : metrics) {
            metricUpdater.addMetric(m);
        }
        this.timer.schedule(metricUpdater, 1000, 1000);
	}

	public Timer getTimer() {
		return this.timer;
	}

}