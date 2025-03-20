package dev.corgitaco.dataanchor.levelgen;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.level.ServerLevelTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class CanyonLevelTrackedData extends ServerLevelTrackedData {

    private final CanyonStorage canyonStorage = new CanyonStorage();

    public static final TrackedDataKey<CanyonLevelTrackedData> KEY = TrackedDataRegistries.LEVEL.register(ResourceLocation.fromNamespaceAndPath(DataAnchor.MOD_ID, "canyon"), CanyonLevelTrackedData.class,
            (key, obj) -> obj instanceof ServerLevel serverLevel ? new CanyonLevelTrackedData(key, serverLevel) : null
    );


    public CanyonLevelTrackedData(TrackedDataKey<? extends ServerLevelTrackedData> trackedDataKey, ServerLevel serverLevel) {
        super(trackedDataKey, serverLevel);
    }

    @Override
    public @Nullable CompoundTag save() {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
    }

    public CanyonStorage getCanyonStorage() {
        return canyonStorage;
    }
}
