package com.crawl.backend.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface DAO<T extends Identifiable<K>, K extends Serializable> {
    Optional<T> get(K id);

    Collection<T> getAll();

    boolean exists(K id);

    T save(T t);

    void update(T t);

    void delete(T t);

    void purge();
}
