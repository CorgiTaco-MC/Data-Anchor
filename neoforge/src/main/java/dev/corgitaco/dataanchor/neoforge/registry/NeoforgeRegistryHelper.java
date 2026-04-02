package dev.corgitaco.dataanchor.neoforge.registry;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.corgitaco.dataanchor.registry.RegistryHelper;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@AutoService(RegistryHelper.class)
public class NeoforgeRegistryHelper implements RegistryHelper{
    public static final Map<ResourceKey<?>, DeferredRegister> CACHED = new Reference2ObjectOpenHashMap<>();

    @Override
    public <T> Supplier<T> register(Registry<T> registry, Identifier location, Supplier<T> value) {
        return CACHED.computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().identifier(), location.getNamespace())).register(location.getPath(), value);
    }

    public static final List<Consumer<NewRegistryEvent>> NEW_REGISTRIES = new ArrayList<>();
    @Override
    public <T> Supplier<Registry<T>> createSimpleBuiltin(ResourceKey<Registry<T>> registryKey) {
        Registry<T> ts = new RegistryBuilder<>(registryKey).sync(true).create();
        NEW_REGISTRIES.add(newRegistryEvent -> newRegistryEvent.register(ts));
        return () -> ts;
    }

    public static final List<Consumer<DataPackRegistryEvent.NewRegistry>> DATAPACK_REGISTRIES = new ArrayList<>();

    @Override
    public <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Supplier<Codec<T>> codec) {
        DATAPACK_REGISTRIES.add(newRegistry -> newRegistry.dataPackRegistry(key, codec.get()));
    }
}
