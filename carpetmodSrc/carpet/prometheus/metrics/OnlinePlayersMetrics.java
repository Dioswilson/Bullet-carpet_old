package carpet.prometheus.metrics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import carpet.prometheus.PrometheusExtension;

public class OnlinePlayersMetrics extends AbstractMetric {
	
	public OnlinePlayersMetrics(String name, String help) {
        super(name, help, "world", "name", "uuid");
    }

    @Override
    public void update(PrometheusExtension extension) {
        for (WorldServer world : extension.getServer().worlds) {
        	for (EntityPlayer player : world.playerEntities) {
                this.getGauge().labels(world.provider.getDimensionType().getName(), player.getName(), player.getUniqueID().toString()).set(1);
            }
        }
    }

}