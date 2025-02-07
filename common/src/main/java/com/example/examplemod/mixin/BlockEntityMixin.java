package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.blockentity.BlockEntityTrackedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Optional;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements TrackedDataContainer<BlockEntity, BlockEntityTrackedData> {

    @Shadow
    @Nullable
    protected Level level;
    @Nullable
    private TrackedDataContainer<BlockEntity, BlockEntityTrackedData> container;


    @Inject(method = "setLevel", at = @At("RETURN"))
    private void setLevel(Level level, CallbackInfo ci) {
        container = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.BLOCK_ENTITY, (BlockEntity) (Object) this, level.isClientSide());
        this.container.create();
    }

    @Override
    public <E extends BlockEntityTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return this.container.get(key);
    }

    @Override
    public void create() {
        this.container.create();
    }

    @Override
    public Collection<TrackedDataKey<BlockEntityTrackedData>> getKeys() {
        return this.container.getKeys();
    }

    @Inject(method = "loadStatic", at = @At("RETURN"))
    private static void loadStatic(BlockPos pos, BlockState state, CompoundTag tag, CallbackInfoReturnable<BlockEntity> cir) {
        if (cir.getReturnValue() instanceof TrackedDataContainer container) {
            if (tag.contains("TrackedData")) {
                CompoundTag trackedData = tag.getCompound("TrackedData");
                Collection<TrackedDataKey<BlockEntityTrackedData>> keys = container.getKeys();
                for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
                    container.get(key).ifPresent(data -> {
                        if (data instanceof BlockEntityTrackedData blockEntityTrackedData) {
                            if (trackedData.contains(key.getId().toString())) {
                                blockEntityTrackedData.load(trackedData.getCompound(key.getId().toString()));
                            }
                        }
                    });
                }
            }
        }
    }

    @Inject(method = "saveWithFullMetadata", at = @At("RETURN"))
    private void saveWithFullMetadata(CallbackInfoReturnable<CompoundTag> cir) {
        if (this.container != null) {
            CompoundTag trackedData = new CompoundTag();
            for (TrackedDataKey<BlockEntityTrackedData> key : this.container.getKeys()) {
                this.container.get(key).ifPresent(data -> {
                    CompoundTag save = data.save();
                    if (save != null) {
                        trackedData.put(key.getId().toString(), save);
                    }
                });
            }
            cir.getReturnValue().put("TrackedData", trackedData);
        }
    }
}
