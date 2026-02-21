package dev.corgitaco.dataanchor.fabric.registry;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.corgitaco.dataanchor.registry.RegistryHelper;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

@AutoService(RegistryHelper.class)
public class FabricRegistryHelper implements RegistryHelper {
    public <T> Supplier<T> register(Registry<T> registry, ResourceLocation id, Supplier<T> value) {
        T value1 = Registry.register(registry, id, value.get());
        return () -> value1;
    }


    @Override
    public <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> registryKey) {
        MappedRegistry<T> registry = FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
        return () -> registry;
    }


    @Override
    public <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Supplier<Codec<T>> codec) {
        DynamicRegistries.registerSynced(key, codec.get());
    }
}
