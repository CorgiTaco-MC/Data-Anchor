/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistry;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrackedDataContainer<O, T extends TrackedData<O>> {

    <E extends T> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key);

    void dataAnchor$createTrackedData();

    Collection<TrackedDataKey<T>> dataAnchor$getTrackedDataKeys();

    static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(TrackedDataRegistry<O, T> registry, O o, boolean isClient) {
        return makeBasicContainer(registry, o, isClient, false);
    }

    static <O, T extends TrackedData<O>> TrackedDataContainer<O, T> makeBasicContainer(TrackedDataRegistry<O, T> registry, O o, boolean isClient, boolean lazyLoad) {
        return new TrackedDataContainer<>() {

            private boolean lazyLoaded = !lazyLoad;
            private final Map<TrackedDataKey<T>, T> trackedDataMap = new Reference2ReferenceOpenHashMap<>();
            private final List<TrackedDataKey<T>> keys = List.copyOf(registry.factories().keySet());

            @Override
            public <E extends T> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
                if (!lazyLoaded) {
                    dataAnchor$createTrackedData();
                    lazyLoaded = true;
                }

                T t = trackedDataMap.get(key);
                if (t == null) {
                    return Optional.empty();
                }
                return Optional.of((E) t);
            }

            @Override
            public void dataAnchor$createTrackedData() {
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
            public Collection<TrackedDataKey<T>> dataAnchor$getTrackedDataKeys() {
                if (!lazyLoaded) {
                    dataAnchor$createTrackedData();
                    lazyLoaded = true;
                }
                return keys;
            }
        };
    }
}
