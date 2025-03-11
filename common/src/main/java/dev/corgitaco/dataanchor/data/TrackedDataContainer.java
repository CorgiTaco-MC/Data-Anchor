package dev.corgitaco.dataanchor.data;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrackedDataContainer<O, T extends TrackedData<O>> {

    <E extends T> Optional<E> get(TrackedDataKey<E> key);

    void create();

    Collection<TrackedDataKey<T>> getKeys();

    static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(TrackedDataRegistry<O, T> registry, O o, boolean isClient) {
        return new TrackedDataContainer<>() {
            private final Map<TrackedDataKey<T>, T> trackedDataMap = new Reference2ReferenceOpenHashMap<>();
            private final List<TrackedDataKey<T>> keys = List.copyOf(registry.factories().keySet());

            @Override
            public <E extends T> Optional<E> get(TrackedDataKey<E> key) {
                T t = trackedDataMap.get(key);
                if (t == null) {
                    return Optional.empty();
                }
                return Optional.of((E) t);
            }

            @Override
            public void create() {
                registry.factories().forEach((key, factory) -> {
                    T trackedData = factory.create(key, o);
                    if (trackedData != null) {
                        if (isClient) {
                            if (trackedData instanceof ClientTrackedData) {
                                trackedDataMap.put(key, trackedData);
                            }
                        } else {
                            if (trackedData instanceof ServerTrackedData) {
                                trackedDataMap.put(key, trackedData);
                            }
                        }
                    }
                });
            }

            @Override
            public Collection<TrackedDataKey<T>> getKeys() {
                return keys;
            }
        };
    }
}
