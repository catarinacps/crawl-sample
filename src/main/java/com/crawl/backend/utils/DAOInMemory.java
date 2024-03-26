package com.crawl.backend.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class DAOInMemory<T extends Identifiable<K>, K extends Serializable> implements DAO<T, K> {
    private final ConcurrentHashMap<K, T> data = new ConcurrentHashMap<>();
    private final ReentrantLock saveLock = new ReentrantLock();
    private final Supplier<K> idGenerator;

    public DAOInMemory(Supplier<K> idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<T> get(K id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Collection<T> getAll() {
        return data.values();
    }

    @Override
    public boolean exists(K id) {
        return data.containsKey(id);
    }

    @Override
    public T save(T t) {
        try {
            saveLock.lock();

            K id;

            do {
                id = idGenerator.get();
            } while (data.containsKey(id));

            t.setId(id);

            data.put(id, t);

            return t;
        } finally {
            saveLock.unlock();
        }
    }

    @Override
    public void update(T t) {
        data.replace(t.getId(), t);
    }

    @Override
    public void delete(T t) {
        data.remove(t.getId());
    }

    @Override
    public void purge() {
        data.clear();
    }
}
