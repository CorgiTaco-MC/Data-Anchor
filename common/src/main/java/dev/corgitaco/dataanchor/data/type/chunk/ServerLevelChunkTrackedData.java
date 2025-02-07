package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract non-sealed class ServerLevelChunkTrackedData extends LevelChunkTrackedData implements ServerTrackedData {

    public ServerLevelChunkTrackedData(TrackedDataKey<? extends LevelChunkTrackedData> trackedDataKey, LevelChunk levelChunk) {
        super(trackedDataKey, levelChunk);
    }
}
