package eu.binflux.config.collection.expiration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class IdentifiedExpiration extends Thread {

    private final AtomicReference<Boolean> threadAlive;
    private final AtomicReference<Long> expireAfterMillis;
    private final AtomicReference<Long> interval;
    private final Map<String, Long> creationMap;
    private final Consumer<String> expirationConsumer;

    public IdentifiedExpiration(long time, TimeUnit timeUnit, long intervalInMillis, Consumer<String> expirationConsumer) {
        this.threadAlive = new AtomicReference<>(true);
        long expiry = TimeUnit.MILLISECONDS.convert(time, timeUnit);
        this.expireAfterMillis = new AtomicReference<>(expiry);
        this.interval = new AtomicReference<>(intervalInMillis);
        this.creationMap = new ConcurrentHashMap<>();
        this.expirationConsumer = expirationConsumer;
        setDaemon(true);
        start();
    }

    private void ensureAlive() {
        if (!threadAlive.get())
            throw new IllegalStateException("ExpiringMap Thread isn't alive!");
    }

    public void addCreation(String key) {
        ensureAlive();
        creationMap.put(key, System.currentTimeMillis());
    }

    @Override
    public void run() {
        try {
            while (threadAlive.get()) {
                final long current = System.currentTimeMillis();
                if (!creationMap.isEmpty())
                    for (String key : creationMap.keySet()) {
                        final long expireAt = creationMap.get(key) + expireAfterMillis.get();
                        if (current > expireAt) {
                            creationMap.remove(key);
                            expirationConsumer.accept(key);
                        }
                    }
                Thread.sleep(interval.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}