package com.crawl.backend.utils;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {
    T getId();

    void setId(T t);
}
