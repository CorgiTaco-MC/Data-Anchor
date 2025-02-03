package com.example.examplemod.mixin;

import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import com.example.examplemod.data.player.PlayerTrackedDataRegistry;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(Player.class)
public class PlayerMixin implements TrackedDataAccess<PlayerTrackedData> {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void createTrackedData(Level level, BlockPos pos, float yRot, GameProfile gameProfile, CallbackInfo ci) {
        create();
    }


    @Unique
    private final Map<TrackedDataKey<PlayerTrackedData>, PlayerTrackedData> exampleMod$trackedDataKeyPlayerTrackedDataReference2ReferenceOpenHashMap = new Reference2ReferenceOpenHashMap<>();
    @Unique
    private final List<TickableTrackedData> exampleMod$tickablePlayerData = new ArrayList<>();


    @Override
    public <E extends PlayerTrackedData> E get(TrackedDataKey<E> key) {
        return (E) exampleMod$trackedDataKeyPlayerTrackedDataReference2ReferenceOpenHashMap.get(key);
    }

    @Override
    public void create() {
        PlayerTrackedDataRegistry.TRACKED_DATA_FACTORIES.forEach((key, factory) -> {
            PlayerTrackedData value = factory.create(key, (Player) (Object) this);
            if (value == null) {
                throw new IllegalArgumentException("No PlayerTrackedData factories are NOT allowed. Found null player PlayerTrackedData for key \"%s\"".formatted(key.getId()));
            }

            if (value instanceof TickableTrackedData tickableData) {
                exampleMod$tickablePlayerData.add(tickableData);
            }

            exampleMod$trackedDataKeyPlayerTrackedDataReference2ReferenceOpenHashMap.put((TrackedDataKey<PlayerTrackedData>) key, value);
        });
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        for (TickableTrackedData tickablePlayerDatum : this.exampleMod$tickablePlayerData) {
            tickablePlayerDatum.tick();
        }
    }

    @Override
    public Collection<TrackedDataKey<PlayerTrackedData>> getKeys() {
        return this.exampleMod$trackedDataKeyPlayerTrackedDataReference2ReferenceOpenHashMap.keySet();
    }
}
