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

    public static void initialize() throws IllegalStateException {
        impl.initialize();
    }

    public static void reset() {
        impl.reset();
    }

    public interface Implementation {

        void initialize(Plugin plugin) throws IllegalStateException;

        void reset();

        ServiceProvider of(WirerPlugin plugin);

    }


}
