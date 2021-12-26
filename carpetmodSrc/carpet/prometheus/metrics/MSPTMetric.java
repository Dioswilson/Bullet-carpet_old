package carpet.prometheus.metrics;

import net.minecraft.util.math.MathHelper;
import carpet.prometheus.PrometheusExtension;

public class MSPTMetric extends AbstractMetric {
	
	public MSPTMetric(String name, String help) {
        super(name, help);
    }

    @Override
    public void update(PrometheusExtension extension) {
        double MSPT = MathHelper.average(extension.getServer().tickTimeArray) * 1.0E-6D;
        this.getGauge().set(MSPT);
    }

}