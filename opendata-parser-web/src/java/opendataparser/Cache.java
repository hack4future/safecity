package opendataparser;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Moxa
 */
public class Cache<K, V> {

    private final ConcurrentHashMap<K, Holder<V>> cache = new ConcurrentHashMap<K, Holder<V>>();
    private final ConcurrentLinkedQueue<Entry<K, Long>> timings = new ConcurrentLinkedQueue<Entry<K, Long>>();
    private long lifetime;
    private Computable<K, V> computable;

    public Cache(long lifetimeSec, long checkPeriodSec, Computable<K, V> computable) {
        this.lifetime = lifetimeSec * 1000;
        this.computable = computable;
        new CacheControl(checkPeriodSec).start();
    }

    public Cache(long lifetimeSec, long checkPeriodSec) {
        this(lifetimeSec, checkPeriodSec, null);
    }

    public Cache(long lifetimeSec, Computable<K, V> computable) {
        this(lifetimeSec, lifetimeSec / 2, computable);
    }

    public Cache(long lifetimeSec) {
        this(lifetimeSec, lifetimeSec / 2);
    }

    public V get(K k) {
        return getFromCache(k, computable, false);
    }

    public V get(K k, boolean updateTTL) {
        return getFromCache(k, computable, updateTTL);
    }

    public V get(K k, Computable<K, V> computable) {
        return getFromCache(k, computable, false);
    }

    public V get(K k, Computable<K, V> computable, boolean updateTTL) {
        return getFromCache(k, computable, updateTTL);
    }

    public static interface Computable<K, V> {

        public V compute(K k);
    }

    public void clear() {
        timings.clear();
        cache.clear();
    }

    private class Holder<V> {

        protected V v;
        protected boolean done = false;

        public Holder() {
        }

        public Holder(V v) {
            this.v = v;
            done = true;
        }

        public V get() {
            if (!done) {
                synchronized (this) {
                    while (!done) {
                        try {
                            this.wait();
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                }
            }
            return v;
        }

        public void run(Computable<K, V> c, K k) {
            v = c.compute(k);
            synchronized (this) {
                done = true;
                this.notifyAll();
            }
        }
    }

    private V getFromCache(final K key, Computable<K, V> c, boolean updateTTL) {
        Holder<V> f = cache.get(key);
        if (f == null) {
            if (c == null) {
                return null;
            }
            Holder<V> ft = new Holder<V>();
            f = cache.putIfAbsent(key, ft);
            if (f == null) {
                f = ft;
                ft.run(c, key);
                updateTimingCache(key);
            }
        } else if (updateTTL) {
            updateTimingCache(key);
        }
        return f.get();
    }

    public void put(final K key, final V value) {
        cache.put(key, new Holder<V>(value));
        updateTimingCache(key);
    }

    public boolean putIfAbsent(final K key, final V value) {
        if (cache.putIfAbsent(key, new Holder<V>(value)) == null) {
            updateTimingCache(key);
            return true;
        }
        return false;
    }

    private void updateTimingCache(final K key) {
        final Long timing = lifetime + System.currentTimeMillis();
        timings.add(new Entry<K, Long>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public Long getValue() {
                return timing;
            }

            @Override
            public Long setValue(Long value) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public int size() {
        return cache.size();
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    private class CacheControl extends Thread {

        private long checkPeriod;
        private boolean enabled = true;

        public CacheControl(long checkPeriodSec) {
            setDaemon(true);
            this.checkPeriod = checkPeriodSec * 1000;
            if (checkPeriodSec <= 0) {
                enabled = false;
            }
        }

        @Override
        public void run() {
            Entry<K, Long> entry = null;
            while (enabled) {
                try {
                    Thread.sleep(checkPeriod);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                }
                Long time = System.currentTimeMillis();
                while ((entry = timings.peek()) != null && entry.getValue().compareTo(time) < 0) {
                    cache.remove(timings.poll().getKey());
                }
            }
        }
    }
}
