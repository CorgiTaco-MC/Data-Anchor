package dev.corgitaco.dataanchor.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkBlockStateInterceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {ProtoChunk.class, LevelChunk.class})
public class ChunkMixin implements ChunkBlockStateInterceptor.Internal {


    @WrapMethod(method = "setBlockState")
    private BlockState dataAnchor$IncterceptSetBlockState(BlockPos pos, BlockState originalState, int flags, Operation<BlockState> original) {
        BlockState replacement = dataAnchor$getInterceptorState(pos, originalState, originalState, flags);
        return original.call(pos, replacement, flags);
    }
}