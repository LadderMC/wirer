package fr.ladder.wirer.base;

import fr.ladder.reflex.Reflex;
import fr.ladder.wirer.ServiceProvider;
import fr.ladder.wirer.Wirer;
import fr.ladder.wirer.annotation.Inject;
import fr.ladder.wirer.plugin.WirerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * @author Snowtyy
 **/
public class WirerInjector implements Wirer.Implementation {

    private final BiConsumer<LogLevel, String> log;

    private final Map<Class<?>, Object> singletonMap;
    
    private final Map<Class<?>, Object> transientMap;
    
    private final Map<WirerPlugin, WirerServiceContainer> serviceContainerMap;

    private boolean initialized = false;

    public WirerInjector(BiConsumer<LogLevel, String> log) {
        this.log = log;
        singletonMap = new ConcurrentHashMap<>();
        transientMap = new ConcurrentHashMap<>();
        serviceContainerMap = new HashMap<>();
    }

    /**
     * @deprecated Use {@link WirerInjector#WirerInjector(BiConsumer)} instead.
     * @param logger The logger to use.
     */
    @Deprecated(forRemoval = true)
    public WirerInjector(Logger logger) {
        this((level, message) -> {
            switch (level) {
                case INFO ->  logger.info(message);
                case WARN -> logger.warning(message);
            }
        });
    }

    /**
     * Default logging system with plugin logger.
     * @param plugin The plugin to use.
     */
    public WirerInjector(Plugin plugin) {
        this((level, message) -> {
            switch (level) {
                case INFO ->  plugin.getLogger().info(message);
                case WARN -> plugin.getLogger().warning(message);
            }
        });
    }

    public synchronized void init() throws IllegalStateException {
        if(initialized)
            throw new IllegalStateException("Wirer is already initialized.");

        initialized = true;
        log.accept(LogLevel.INFO, "Initialize wirer injection.");

        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if(!(p.isEnabled() && p instanceof WirerPlugin plugin))
                continue;
            
            // création du "scoped service collection"
            var serviceCollection = serviceContainerMap.computeIfAbsent(plugin,
                    _ -> new WirerServiceContainer(singletonMap, transientMap));

            // bindings par défaut
            serviceCollection.addScoped(plugin);
            serviceCollection.addScoped(plugin.getLogger());
            plugin.registerServices(serviceCollection);
        }
    }

    public synchronized void injectAll() throws IllegalStateException {
        this.ensureInitialized();
        log.accept(LogLevel.INFO, "Start wirer injection.");
        Instant start = Instant.now();
        serviceContainerMap.forEach(this::injectContainer);
        Duration duration = Duration.between(start, Instant.now());
        log.accept(LogLevel.INFO, "| Injection done!");
        log.accept(LogLevel.INFO, "| > time: " + (duration.toNanos() / 10000) / 100D + "ms");
    }

    public synchronized void ejectAll() throws IllegalStateException {
        this.ensureInitialized();
        log.accept(LogLevel.INFO, "Start wirer cleanup.");
        Instant start = Instant.now();
        serviceContainerMap.forEach(this::ejectContainer);
        Duration duration = Duration.between(start, Instant.now());
        log.accept(LogLevel.INFO, "| Cleanup done!");
        log.accept(LogLevel.INFO, "| > time: " + (duration.toNanos() / 10000) / 100D + "ms");
    }

    @Override
    public synchronized ServiceProvider getProvider(WirerPlugin plugin) throws IllegalStateException {
        this.ensureInitialized();
        return serviceContainerMap.get(plugin);
    }

    private void injectContainer(WirerPlugin plugin, ServiceProvider serviceProvider) {
        try (var inspector = Reflex.getInspector(plugin)) {
            inspector.getFieldsWithAnnotation(Inject.class).forEach(field -> {
                if(!Modifier.isPrivate(field.getModifiers()) || !Modifier.isStatic(field.getModifiers()))
                    return;
                serviceProvider.get(field.getType()).ifPresent(obj -> {
                    try {
                        field.setAccessible(true);
                        field.set(null, obj);
                    } catch (IllegalAccessException _) {
                        String objName = obj.getClass().getSimpleName();
                        String fieldName = field.getDeclaringClass().getSimpleName() + "#" + field.getName();
                        log.accept(LogLevel.WARN, "An error occurred while injecting " + objName + " in " + fieldName);
                    }
                });
            });
        }
    }

    private void ejectContainer(WirerPlugin plugin, ServiceProvider serviceProvider) {
        try (var inspector = Reflex.getInspector(plugin)) {
            inspector.getFieldsWithAnnotation(Inject.class).forEach(field -> {
                if(!Modifier.isPrivate(field.getModifiers()) || !Modifier.isStatic(field.getModifiers()))
                    return;
                try {
                    field.setAccessible(true);
                    field.set(null, null);
                } catch (IllegalAccessException _) {
                    String objName = "unknown";
                    try {
                        objName = field.get(null).getClass().getSimpleName();
                    } catch (IllegalAccessException _) {}
                    String fieldName = field.getDeclaringClass().getSimpleName() + "#" + field.getName();
                    log.accept(LogLevel.WARN, "An error occurred while injecting " + objName + " in " + fieldName);
                }
            });
        }
    }

    private void ensureInitialized() throws IllegalStateException {
        if(!initialized)
            throw new IllegalStateException("Wirer isn't initialized.");
    }

    public enum LogLevel {
        INFO,
        WARN
    }

}
