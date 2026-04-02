/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.registry;

import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrackedDataRegistry<O, T extends TrackedData<O>> {

    public static final Map<Identifier, TrackedDataRegistry<?, ?>> REGISTRIES = new HashMap<>();

    private final Map<TrackedDataKey<T>, TrackedDataFactory<O, T>> factories = new Reference2ObjectOpenHashMap<>();
    private final Set<Identifier> usedIds = new HashSet<>();
    private final Identifier id;

    private TrackedDataRegistry(Identifier id) {
        this.id = id;
    }

    public static <O, T extends TrackedData<O>> TrackedDataRegistry<O, T> of(Identifier id) {
        TrackedDataRegistry<?, ?> trackedDataRegistry = REGISTRIES.computeIfAbsent(id, key -> new TrackedDataRegistry<>(id));
        return (TrackedDataRegistry<O, T>) trackedDataRegistry;
    }

    public <F extends T, K extends TrackedDataKey<F>> void register(K key, TrackedDataFactory<O, F> factory) {
        if (factories.get(key) != null) {
            throw new IllegalArgumentException("TrackedDataKey already registered in Tracked Data Registry \"%s\"!");
        } else {
            Identifier id = key.getId();
            if (usedIds.contains(id)) {
                throw new IllegalArgumentException("TrackedDataKey with id \"%s\" already registered in Tracked Data Registry \"%s\"!".formatted(id, this.id));
            }

            factories.put((TrackedDataKey<T>) key, (TrackedDataFactory<O, T>) factory);
            usedIds.add(id);
        }

    }

    public <E extends T> Optional<E> get(TrackedDataKey<E> key, O o) {
        if (o instanceof TrackedDataContainer trackedDataContainer) {
            return trackedDataContainer.dataAnchor$getTrackedData(key);
        }

        return Optional.empty();
    }

    @Nullable
    public TrackedDataContainer<O, T> getContainer(O o) {
        if (o instanceof TrackedDataContainer trackedDataContainer) {
            return (TrackedDataContainer<O, T>) trackedDataContainer;
        }

        return null;
    }

    public Map<TrackedDataKey<T>, TrackedDataFactory<O, T>> factories() {
        return factories;
    }

    public <E extends T> TrackedDataKey<E> register(Identifier id, Class<E> clazz, TrackedDataFactory<O, E> factory) {
        TrackedDataKey<E> key = (TrackedDataKey<E>) TrackedDataKey.of(this, clazz, id);
        if (factories.get(key) == null) {
            register(key, factory);
        }
        return key;
    }


    @FunctionalInterface
    public interface TrackedDataFactory<T, D extends TrackedData<T>> {
        @Nullable D create(TrackedDataKey<D> key, T obj);
    }
}
