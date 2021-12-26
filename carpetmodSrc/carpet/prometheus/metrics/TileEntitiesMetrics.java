package carpet.prometheus.metrics;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import carpet.prometheus.PrometheusExtension;

public class TileEntitiesMetrics extends AbstractMetric {
	
    public TileEntitiesMetrics(String name, String help) {
        super(name, help, "world", "type");
    }

    @Override
    public void update(PrometheusExtension extension) {
    	HashMap<String, HashMap<String, Integer>> worldTileEntities = new HashMap<>();
    	for (WorldServer world : extension.getServer().worlds) {
    		List<TileEntity> TileEntityList = world.loadedTileEntityList;
    		String dimensionName = world.provider.getDimensionType().getName();
    		for (TileEntity tileEntity : TileEntityList) {
    			String tileEntityName = TileEntity.getKey(tileEntity.getClass()).getPath();
    			if (!worldTileEntities.containsKey(dimensionName)) {
    				worldTileEntities.put(dimensionName, new HashMap<>());
                }
    			worldTileEntities.get(dimensionName).put(tileEntityName, worldTileEntities.get(dimensionName).getOrDefault(tileEntityName, 0) + 1);
    		}
        }
    	worldTileEntities.forEach((world, tileEntities) -> tileEntities.forEach((tileEntityName, count) -> this.getGauge().labels(world, tileEntityName).set(count)));
    }

}