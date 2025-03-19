package dev.corgitaco.dataanchor.data.type.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ChunkBlockStateInterceptor {

    @Nullable
    default BlockState getNewState(BlockPos pos, BlockState original, @Nullable BlockState lastState, boolean isMoving) {
        return original;
    }

    interface Internal {
        default BlockState dataAnchor$getInterceptorState(BlockPos pos, BlockState original, @Nullable BlockState lastState, boolean isMoving) {
            return original;
        }
    }
}
