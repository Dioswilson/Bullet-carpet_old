package carpet.prometheus.metrics;

import net.minecraft.world.WorldServer;
import carpet.prometheus.PrometheusExtension;

public class LoadedChunksMetrics extends AbstractMetric {
	
	public LoadedChunksMetrics(String name, String help) {
        super(name, help, "world");
    }

    @Override
    public void update(PrometheusExtension extension) {
    	for (WorldServer world : extension.getServer().worlds) {
            this.getGauge().labels(world.provider.getDimensionType().getName()).set(world.getChunkProvider().getLoadedChunkCount());
        }
    }

}