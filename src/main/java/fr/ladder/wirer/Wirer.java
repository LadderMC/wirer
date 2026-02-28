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

    public static void initialize(Plugin engine) throws IllegalStateException {
        impl.initialize(engine);
    }
    
    public interface Implementation {

        void initialize(Plugin engine) throws IllegalStateException;

        ServiceProvider of(WirerPlugin plugin);

    }


}
