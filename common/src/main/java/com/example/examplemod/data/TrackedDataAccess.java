package com.example.examplemod.data;

import java.util.Collection;

public interface TrackedDataAccess<T extends TrackedData> {
    <E extends T> E get(TrackedDataKey<E> key);

    void create();

    Collection<TrackedDataKey<T>> getKeys();
}
