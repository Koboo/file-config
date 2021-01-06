package eu.binflux.config.collection;

import eu.binflux.config.collection.expiration.IdentifiedExpiration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ExpiringMap<V> extends ConcurrentHashMap<String, V> {

    private final IdentifiedExpiration identifiedExpiration;

    public ExpiringMap(long time, TimeUnit timeUnit, int intervalInMillis) {
        this.identifiedExpiration = new IdentifiedExpiration(time, timeUnit, intervalInMillis, this::remove);
    }

    @Override
    public V put(String key, V value) {
        identifiedExpiration.addCreation(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (String key : m.keySet()) {
            identifiedExpiration.addCreation(key);
        }
        super.putAll(m);
    }

    @Override
    public V putIfAbsent(String key, V value) {
        if (!containsKey(key))
            identifiedExpiration.addCreation(key);
        return super.putIfAbsent(key, value);
    }


}
