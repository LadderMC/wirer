package fr.ladder.wirer;

import fr.ladder.wirer.plugin.WirerPlugin;

/**
 * @author Snowtyy
 */
public class Wirer {

    private static Implementation impl;
    
    private Wirer() {
        throw new UnsupportedOperationException("This is static class and cannot be instantiated");
    }

    public static void defineImpl(Implementation implementation) {
        impl = implementation;
    }

    public static ServiceProvider getProvider(WirerPlugin plugin) throws IllegalStateException {
        return impl.getProvider(plugin);
    }

    public interface Implementation {

        ServiceProvider getProvider(WirerPlugin plugin);

    }


}
