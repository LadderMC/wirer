package fr.ladder.wirer.base;

import fr.ladder.wirer.ScopedServiceCollection;
import fr.ladder.wirer.ServiceCollection;
import fr.ladder.wirer.ServiceProvider;
import fr.ladder.wirer.Wirer;
import fr.ladder.wirer.plugin.WirerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Snowtyy
 */
public class WirerInjector implements Wirer.Implementation {

    private boolean _initialized;

    private final Map<Class<? extends WirerPlugin>, WirerScopedServiceProvider> _scopedProviderMap;

    public WirerInjector() {
        _initialized = false;
        _scopedProviderMap = new ConcurrentHashMap<>();
    }

    @Override
    public void initialize(Plugin engine) throws IllegalStateException {
        if(_initialized)
            throw new IllegalStateException("Already initialized");
        _initialized = true;

        WirerServiceCollection parent = new WirerServiceCollection();
        this.prepare(parent, engine.getLogger());

    }

    private void prepare(WirerServiceCollection parent, Logger logger) {
        logger.info("Dependency injection with Wirer:");

        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if(!(p.isEnabled() && p instanceof WirerPlugin plugin))
                continue;
            // création du "scoped service collection"
            var serviceCollection = new WirerScopedServiceCollection(parent);
            map.put(plugin, serviceCollection);

            // bindings par défaut
            serviceCollection.addScoped(plugin);
            serviceCollection.addScoped(plugin.getLogger());
            plugin.registerServices(serviceCollection);
            this.registerAll(plugin, serviceCollection);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public ServiceProvider of(WirerPlugin plugin) {
        return null;
    }
}
