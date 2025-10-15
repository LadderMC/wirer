package fr.ladder.wirer;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * @author Snowtyy
 */
public final class Injector {

    private static Implementation _impl;

    public static void setImplementation(Implementation impl) {
        _impl = impl;
    }

    private Injector() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void setup(JavaPlugin plugin, Consumer<ScopedServiceCollection> consumer) {
        _impl.setup(plugin, consumer);
    }

    public static void setup(JavaPlugin plugin) {
        _impl.setup(plugin,services -> {});
    }

    public interface Implementation {

        void setup(JavaPlugin plugin, Consumer<ScopedServiceCollection> consumer);
        
    }

}
