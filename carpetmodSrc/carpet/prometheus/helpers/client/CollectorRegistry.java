package carpet.prometheus.helpers.client;

import carpet.prometheus.helpers.client.Collector.Describable;
import carpet.prometheus.helpers.client.Collector.MetricFamilySamples;
import carpet.prometheus.helpers.client.Collector.MetricFamilySamples.Sample;
import carpet.prometheus.helpers.client.SampleNameFilter.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;


public class CollectorRegistry {
    public static final CollectorRegistry defaultRegistry = new CollectorRegistry(true);
    private final Object namesCollectorsLock;
    private final Map<Collector, List<String>> collectorsToNames;
    private final Map<String, Collector> namesToCollectors;
    private final boolean autoDescribe;

    public CollectorRegistry() {
        this(false);
    }

    public CollectorRegistry(boolean autoDescribe) {
        this.namesCollectorsLock = new Object();
        this.collectorsToNames = new HashMap();
        this.namesToCollectors = new HashMap();
        this.autoDescribe = autoDescribe;
    }

    public void register(Collector m) {
        List<String> names = this.collectorNames(m);
        this.assertNoDuplicateNames(m, names);
        synchronized(this.namesCollectorsLock) {
            Iterator var4 = names.iterator();

            String name;
            while(var4.hasNext()) {
                name = (String)var4.next();
                if (this.namesToCollectors.containsKey(name)) {
                    throw new IllegalArgumentException("Failed to register Collector of type " + m.getClass().getSimpleName() + ": " + name + " is already in use by another Collector of type " + ((Collector)this.namesToCollectors.get(name)).getClass().getSimpleName());
                }
            }

            var4 = names.iterator();

            while(var4.hasNext()) {
                name = (String)var4.next();
                this.namesToCollectors.put(name, m);
            }

            this.collectorsToNames.put(m, names);
        }
    }

    private void assertNoDuplicateNames(Collector m, List<String> names) {
        Set<String> uniqueNames = new HashSet();
        Iterator var4 = names.iterator();

        String name;
        do {
            if (!var4.hasNext()) {
                return;
            }

            name = (String)var4.next();
        } while(uniqueNames.add(name));

        throw new IllegalArgumentException("Failed to register Collector of type " + m.getClass().getSimpleName() + ": The Collector exposes the same name multiple times: " + name);
    }

    public void unregister(Collector m) {
        synchronized(this.namesCollectorsLock) {
            List<String> names = (List)this.collectorsToNames.remove(m);
            Iterator var4 = names.iterator();

            while(var4.hasNext()) {
                String name = (String)var4.next();
                this.namesToCollectors.remove(name);
            }

        }
    }

    public void clear() {
        synchronized(this.namesCollectorsLock) {
            this.collectorsToNames.clear();
            this.namesToCollectors.clear();
        }
    }

    private Set<Collector> collectors() {
        synchronized(this.namesCollectorsLock) {
            return new HashSet(this.collectorsToNames.keySet());
        }
    }

    private List<String> collectorNames(Collector m) {
        List mfs;
        if (m instanceof Collector.Describable) {
            mfs = ((Collector.Describable)m).describe();
        } else if (this.autoDescribe) {
            mfs = m.collect();
        } else {
            mfs = Collections.emptyList();
        }

        List<String> names = new ArrayList();
        Iterator var4 = mfs.iterator();

        while(var4.hasNext()) {
            Collector.MetricFamilySamples family = (Collector.MetricFamilySamples)var4.next();
            names.addAll(Arrays.asList(family.getNames()));
        }

        return names;
    }

    public Enumeration<Collector.MetricFamilySamples> metricFamilySamples() {
        return new CollectorRegistry.MetricFamilySamplesEnumeration();
    }

    /*public Enumeration<Collector.MetricFamilySamples> filteredMetricFamilySamples(Set<String> includedNames) {
        return new CollectorRegistry.MetricFamilySamplesEnumeration(new Builder().nameMustBeEqualTo(includedNames).build());
    }*///Don't need this


    public Enumeration<Collector.MetricFamilySamples> filteredMetricFamilySamples(Predicate<String> sampleNameFilter) {
        return new CollectorRegistry.MetricFamilySamplesEnumeration(sampleNameFilter);
    }

    public Double getSampleValue(String name) {
        return this.getSampleValue(name, new String[0], new String[0]);
    }

    public Double getSampleValue(String name, String[] labelNames, String[] labelValues) {
        Iterator var4 = Collections.list(this.metricFamilySamples()).iterator();

        while(var4.hasNext()) {
            Collector.MetricFamilySamples metricFamilySamples = (Collector.MetricFamilySamples)var4.next();
            Iterator var6 = metricFamilySamples.samples.iterator();

            while(var6.hasNext()) {
                Collector.MetricFamilySamples.Sample sample = (Collector.MetricFamilySamples.Sample)var6.next();
                if (sample.name.equals(name) && Arrays.equals(sample.labelNames.toArray(), labelNames) && Arrays.equals(sample.labelValues.toArray(), labelValues)) {
                    return sample.value;
                }
            }
        }

        return null;
    }

    class MetricFamilySamplesEnumeration implements Enumeration<Collector.MetricFamilySamples> {
        private final Iterator<Collector> collectorIter;
        private Iterator<Collector.MetricFamilySamples> metricFamilySamples;
        private Collector.MetricFamilySamples next;
        private final Predicate<String> sampleNameFilter;

        MetricFamilySamplesEnumeration(Predicate<String> sampleNameFilter) {
            this.sampleNameFilter = sampleNameFilter;
            this.collectorIter = this.filteredCollectorIterator();
            this.findNextElement();
        }

        private Iterator<Collector> filteredCollectorIterator() {
            if (this.sampleNameFilter == null) {
                return CollectorRegistry.this.collectors().iterator();
            } else {
                HashSet<Collector> collectors = new HashSet();
                synchronized(CollectorRegistry.this.namesCollectorsLock) {
                    Iterator var3 = CollectorRegistry.this.namesToCollectors.entrySet().iterator();

                    while(var3.hasNext()) {
                        Map.Entry<String, Collector> entry = (Map.Entry)var3.next();
                        if (this.sampleNameFilter.test(entry.getKey())) {
                            collectors.add(entry.getValue());
                        }
                    }

                    return collectors.iterator();
                }
            }
        }

        MetricFamilySamplesEnumeration() {
            this((Predicate)null);
        }

        private void findNextElement() {
            this.next = null;

            while(this.metricFamilySamples != null && this.metricFamilySamples.hasNext()) {
                this.next = ((Collector.MetricFamilySamples)this.metricFamilySamples.next()).filter(this.sampleNameFilter);
                if (this.next != null) {
                    return;
                }
            }

            while(this.collectorIter.hasNext()) {
                this.metricFamilySamples = ((Collector)this.collectorIter.next()).collect(this.sampleNameFilter).iterator();

                while(this.metricFamilySamples.hasNext()) {
                    this.next = ((Collector.MetricFamilySamples)this.metricFamilySamples.next()).filter(this.sampleNameFilter);
                    if (this.next != null) {
                        return;
                    }
                }
            }

        }

        public Collector.MetricFamilySamples nextElement() {
            Collector.MetricFamilySamples current = this.next;
            if (current == null) {
                throw new NoSuchElementException();
            } else {
                this.findNextElement();
                return current;
            }
        }

        public boolean hasMoreElements() {
            return this.next != null;
        }
    }
}

