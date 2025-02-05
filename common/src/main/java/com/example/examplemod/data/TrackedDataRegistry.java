package com.example.examplemod.data;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrackedDataRegistry<O, T extends TrackedData<O>> {

    public static final Map<ResourceLocation, TrackedDataRegistry<?, ?>> REGISTRIES = new HashMap<>();

    private final Map<TrackedDataKey<T>, TrackedDataFactory<O, T>> factories = new Reference2ObjectOpenHashMap<>();
    private final Set<ResourceLocation> usedIds = new HashSet<>();
    private final ResourceLocation id;

    private TrackedDataRegistry(ResourceLocation id) {
        this.id = id;
    }

    public static <O, T extends TrackedData<O>> TrackedDataRegistry<O, T> of(ResourceLocation id) {
        TrackedDataRegistry<?, ?> trackedDataRegistry = REGISTRIES.computeIfAbsent(id, key -> new TrackedDataRegistry<>(id));
        return (TrackedDataRegistry<O, T>) trackedDataRegistry;
    }

    public <F extends T, K extends TrackedDataKey<F>> void register(K key, TrackedDataFactory<O, F> factory) {
        if (factories.get(key) != null) {
            throw new IllegalArgumentException("TrackedDataKey already registered in Tracked Data Registry \"%s\"!");
        } else {
            ResourceLocation id = key.getId();
            if (usedIds.contains(id)) {
                throw new IllegalArgumentException("TrackedDataKey with id \"%s\" already registered in Tracked Data Registry \"%s\"!".formatted(id, this.id));
            }

            factories.put((TrackedDataKey<T>) key, (TrackedDataFactory<O, T>) factory);
            usedIds.add(id);
        }

    }

    @Nullable
    public T get(TrackedDataKey<? extends T> key, O o) {
        if (o instanceof TrackedDataContainer trackedDataContainer) {
            return (T) trackedDataContainer.get(key);
        }

        return null;
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

    public <E extends T> TrackedDataKey<E> register(ResourceLocation id, Class<E> clazz, TrackedDataFactory<O, E> factory) {
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
