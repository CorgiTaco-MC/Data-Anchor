package com.example.examplemod.data;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class TrackedDataKey<T extends TrackedData> {

    private static final Map<Class<? extends TrackedData>, TrackedDataKey<? extends TrackedData>> TRACKED_DATA = new Reference2ObjectOpenHashMap<>();
    private static final Map<ResourceLocation, TrackedDataKey<? extends TrackedData>> TRACKED_DATA_IDS = new HashMap<>();
    private final ResourceLocation id;


    private TrackedDataKey(ResourceLocation id) {
        this.id = id;
    }

    public static <T extends TrackedData> TrackedDataKey<T> of(Class<T> clazz, ResourceLocation id) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("TrackedData class must not be an interface");
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("TrackedData class must not be an abstract class!");
        }

        TrackedDataKey<? extends TrackedData> trackedDataKey = TRACKED_DATA.get(clazz);
        if (trackedDataKey != null) {
            return (TrackedDataKey<T>) trackedDataKey;
        } else {
            if (TRACKED_DATA_IDS.containsKey(id)) {
                throw new IllegalArgumentException("Attempted to register a key with a duplicate ID");
            }

            TrackedDataKey<T> key = new TrackedDataKey<>(id);
            TRACKED_DATA.put(clazz, key);
            TRACKED_DATA_IDS.put(id, key);
            return key;
        }
    }

    public static <T extends TrackedData, K extends TrackedDataKey<T>> K fromID(ResourceLocation id) {
        return (K) TRACKED_DATA_IDS.get(id);
    }

    public ResourceLocation getId() {
        return id;
    }
}
