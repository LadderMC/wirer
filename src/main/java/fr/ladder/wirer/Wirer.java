package fr.ladder.wirer;

import fr.ladder.wirer.plugin.WirerPlugin;
import org.bukkit.plugin.Plugin;

/**
 * @author Snowtyy
 */
public class Wirer {

    private static Implementation impl;
    
    private Wirer() {
        throw new UnsupportedOperationException("This is static class and cannot be instantiated");
    }

    public static void setImplementation(Implementation implementation) {
        impl = implementation;
    }

    public static void init() throws IllegalStateException {
        impl.init();
    }

    public static void injectAll() throws IllegalStateException {
        impl.injectAll();
    }

    public static void ejectAll() throws IllegalStateException {
        impl.ejectAll();
    }

    public static ServiceProvider of(WirerPlugin plugin) throws IllegalStateException {
        return impl.of(plugin);
    }

    public interface Implementation {

        void init() throws IllegalStateException;

        void injectAll() throws IllegalStateException;

        void ejectAll() throws IllegalStateException;

        ServiceProvider of(WirerPlugin plugin);

    }


}
