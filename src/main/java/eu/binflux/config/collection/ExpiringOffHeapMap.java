package eu.binflux.config.collection;

import eu.binflux.config.collection.expiration.IdentifiedExpiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExpiringOffHeapMap<V> extends OffHeapMap<V> {

    private final IdentifiedExpiration identifiedExpiration;

    public ExpiringOffHeapMap(String cacheName, boolean deleteOnExit, int time, TimeUnit timeUnit, long intervalInMillis) {
        super(cacheName, deleteOnExit);
        this.identifiedExpiration = new IdentifiedExpiration(time, timeUnit, intervalInMillis, this::remove);
    }

    @Override
    public V put(String key, V value) {
        identifiedExpiration.addCreation(key);
        return super.put(key, value);
    }

    @Override
    public V putIfAbsent(String key, V value) {
        identifiedExpiration.addCreation(key);
        return super.putIfAbsent(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for(String key : m.keySet()) {
            identifiedExpiration.addCreation(key);
        }
        super.putAll(m);
    }
}
