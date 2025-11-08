package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author Snowtyy
 */
public class WirerInjector implements fr.ladder.wirer.Injector.Implementation {

    private Set<JavaPlugin> _plugins;

    private WirerServiceCollection _serviceCollection;

    public WirerInjector() {
        _plugins = new HashSet<>();
    }

    @Override
    public void setup(JavaPlugin plugin, Consumer<ServiceCollection> consumer) {
        _plugins.add(plugin);
        _serviceCollection.addAll(plugin);
        // default bindings
        _serviceCollection.addScoped(plugin, plugin);
        _serviceCollection.addScoped(plugin, plugin.getLogger());
        consumer.accept(new WrapperServiceCollection(plugin, _serviceCollection));
    }

    public void runInjection() {
        if(_plugins == null)
            throw new IllegalStateException("Injection has already been run.");

        // ============ INJECTION ============
        final Logger logger = Bukkit.getLogger();
        logger.info("Injecting services...");
        Instant start = Instant.now();
        _plugins.forEach(_serviceCollection::injectAll);
        Duration duration = Duration.between(start, Instant.now());
        logger.info("Injection finished!");
        logger.info("> time: " + (duration.toNanos() / 10000) / 100D + "ms");

        // ============ FREE MEMORY ===========
        _plugins = null;
        _serviceCollection = null;
    }
}
