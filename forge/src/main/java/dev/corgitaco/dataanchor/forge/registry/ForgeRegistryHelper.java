package dev.corgitaco.dataanchor.forge.registry;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.corgitaco.dataanchor.registry.RegistryHelper;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@AutoService(RegistryHelper.class)
public class ForgeRegistryHelper implements RegistryHelper {


    public static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

    @Override
    public <T> Supplier<T> register(Registry<T> registry, ResourceLocation location, Supplier<T> value) {
        return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), location.getNamespace())).register(location.getPath(), value);
    }

    @Override
    public <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> registryKey) {
        if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> mappedRegistry) { // We have to unlock the registry first
            mappedRegistry.unfreeze();
        }

        Registry<T> registry = BuiltInRegistries.registerSimple(registryKey, builder -> new Object());

        if (BuiltInRegistries.REGISTRY instanceof MappedRegistry<? extends Registry<?>> mappedRegistry) { // Relock the registry
            mappedRegistry.freeze();
        }
        return () -> registry;
    }

    public static final List<Consumer<DataPackRegistryEvent.NewRegistry>> DATAPACK_REGISTRIES = new ArrayList<>();

    @Override
    public <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Supplier<Codec<T>> codec) {
        DATAPACK_REGISTRIES.add(newRegistry -> newRegistry.dataPackRegistry(key, codec.get()));
    }
}
