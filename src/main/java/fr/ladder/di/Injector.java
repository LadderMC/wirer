package fr.ladder.di;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * @author Snowtyy
 */
public class Injector {

    private static Implementation impl;

    private Injector() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void implement(Class<?> clazz, Object instance) {
        impl.implement(clazz, instance);
    }

    public static void setupInjection(JavaPlugin plugin, Consumer<ScopedServiceCollection> consumer) {
        impl.setupInjection(plugin, consumer);
    }

    public static void setupInjection(JavaPlugin plugin) {
        impl.setupInjection(plugin, services -> {});
    }

    public interface Implementation {

        void setupInjection(JavaPlugin plugin, Consumer<ScopedServiceCollection> consumer);

        void implement(Class<?> clazz, Object instance);

    }

}
