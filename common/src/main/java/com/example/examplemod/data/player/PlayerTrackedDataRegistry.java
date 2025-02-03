package com.example.examplemod.data.player;

import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerTrackedDataRegistry {
    public static final Map<TrackedDataKey<? extends PlayerTrackedData>, PlayerTrackedDataFactory> TRACKED_DATA_FACTORIES = new Reference2ReferenceOpenHashMap<>();
    public static final Set<ResourceLocation> USED_IDS = new HashSet<>();

    public static <T extends PlayerTrackedData, K extends TrackedDataKey<T>> void register(K key, PlayerTrackedDataFactory factory) {
        if (TRACKED_DATA_FACTORIES.get(key) != null) {
            throw new IllegalArgumentException("TrackedDataKey already registered!");
        } else {
            ResourceLocation id = key.getId();
            if (USED_IDS.contains(id)) {
                throw new IllegalArgumentException("TrackedDataKey with id \"%s\" already registered!".formatted(id));
            }

            TRACKED_DATA_FACTORIES.put(key, factory);
            USED_IDS.add(id);
        }
    }

    public static <E extends PlayerTrackedData> TrackedDataAccess<E> get(Player player) {
        return (TrackedDataAccess<E>) player;
    }

    @Nullable
    public static <E extends PlayerTrackedData> E get(Player player, TrackedDataKey<E> key) {
        if (player instanceof TrackedDataAccess trackedDataAccess) {
            return (E) trackedDataAccess.get(key);
        }

        return null;
    }


    @FunctionalInterface
    public interface PlayerTrackedDataFactory<T extends PlayerTrackedData> {
        @NotNull T create(TrackedDataKey<T> key, Player player);
    }
}
