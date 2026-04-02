package dev.corgitaco.dataanchor.registry;

import com.mojang.serialization.Codec;
import dev.corgitaco.dataanchor.util.ServiceUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface RegistryHelper {

    RegistryHelper INSTANCE = ServiceUtil.load(RegistryHelper.class);

    <T> Supplier<T> register(Registry<T> registry, Identifier id, Supplier<T> value);

    <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> registryKey);

    <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Supplier<Codec<T>> codec);
}
