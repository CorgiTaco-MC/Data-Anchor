package com.example.examplemod.data;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.Collection;
import java.util.Map;

public interface TrackedDataContainer<O, T extends TrackedData<O>> {
    <E extends T> E get(TrackedDataKey<E> key);

    void create();

    Collection<TrackedDataKey<T>> getKeys();

    static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(TrackedDataRegistry<O, T> registry, O o) {
        return new TrackedDataContainer<>() {
            private final Map<TrackedDataKey<T>, T> trackedDataMap = new Reference2ReferenceOpenHashMap<>();


            @Override
            public <E extends T> E get(TrackedDataKey<E> key) {
                return (E) trackedDataMap.get(key);
            }

            @Override
            public void create() {
                registry.factories().forEach((key, factory) -> {
                    T trackedData = factory.create(key, o);
                    if (trackedData == null) {
                        throw new IllegalArgumentException("Null TrackedData factories are NOT allowed. Found null TrackedData for key \"%s\"".formatted(key.getId()));
                    }

                    trackedDataMap.put(key, trackedData);
                });
            }

            @Override
            public Collection<TrackedDataKey<T>> getKeys() {
                return this.trackedDataMap.keySet();
            }
        };
    }
}
