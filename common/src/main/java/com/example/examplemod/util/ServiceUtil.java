package com.example.examplemod.util;

import com.example.examplemod.ExampleMod;

import java.util.ServiceLoader;

public class ServiceUtil {

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ExampleMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
