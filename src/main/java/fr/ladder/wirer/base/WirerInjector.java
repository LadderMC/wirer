package fr.ladder.wirer.base;

import fr.ladder.reflex.PluginInspector;
import fr.ladder.reflex.Reflex;
import fr.ladder.wirer.InjectedPlugin;
import fr.ladder.wirer.ScopedServiceCollection;
import fr.ladder.wirer.ServiceProvider;
import fr.ladder.wirer.annotation.Inject;
import fr.ladder.wirer.annotation.ToInject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Snowtyy
 */
public class WirerInjector {

    private Logger _logger;

    private final WirerServiceCollection _serviceCollection;

    private final Map<Plugin, PluginInspector> _inspectorMap;

    public WirerInjector(Plugin engine) {
        _logger = engine.getLogger();
        _serviceCollection = new WirerServiceCollection();
        _inspectorMap = new HashMap<>();
    }

    private void prepare(Map<Plugin, ServiceProvider> providerMap) {
        _logger.info("Dependency injection with Wirer:");

        Map<Plugin, ScopedServiceCollection> map = new HashMap<>();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if(!(p.isEnabled() && p instanceof InjectedPlugin plugin))
                continue;
            // création du "scoped service collection"
            var serviceCollection = new WirerScopedServiceCollection(_serviceCollection);
            map.put(plugin, serviceCollection);

            // bindings par défaut
            serviceCollection.addScoped(plugin);
            serviceCollection.addScoped(plugin.getLogger());
            plugin.registerServices(serviceCollection);
            this.registerAll(plugin, serviceCollection);
        }

        map.forEach((plugin, serviceCollection) -> providerMap.put(plugin, serviceCollection.toProvider()));
    }

    private void registerAll(Plugin plugin, ScopedServiceCollection serviceCollection) {
        try (var inspector = Reflex.getInspector(plugin)) {
            inspector.getClassesWithAnnotation(ToInject.class)
                    .forEach(tClass -> bind(serviceCollection, tClass));
        }
    }

    @SuppressWarnings("unchecked")
    private <I, T extends I> void bind(ScopedServiceCollection serviceCollection, Class<T> tClass) {
        ToInject toInject = tClass.getAnnotation(ToInject.class);
        Class<I> iClass = (Class<I>) toInject.value();
        if(!iClass.isAssignableFrom(tClass))
            return;
        switch (toInject.type()) {
            case SINGLETON -> serviceCollection.addSingleton(iClass, tClass);
            case SCOPED -> serviceCollection.addScoped(iClass, tClass);
            case TRANSIENT -> serviceCollection.addTransient(iClass, tClass);
        }
    }

    private void injectAll(Map<Plugin, ServiceProvider> map) {
        _logger.info("| Injection in progress...");

        Instant start = Instant.now();
        map.forEach(this::injectAll);
        Duration duration = Duration.between(start, Instant.now());
        _logger.info("| Injection done!");
        _logger.info("| > time: " + (duration.toNanos() / 10000) / 100D + "ms");
    }

    private void clean() {
        _logger = null;
    }

    private void injectAll(Plugin plugin, ServiceProvider serviceProvider) {
        try (var inspector = Reflex.getInspector(plugin)) {
            inspector.getFieldsWithAnnotation(Inject.class)
                    .filter(f -> Modifier.isPrivate(f.getModifiers()) && Modifier.isStatic(f.getModifiers()))
                    .forEach(field -> {
                        serviceProvider.get(field.getType()).ifPresent(obj -> {
                            try {
                                field.setAccessible(true);
                                field.set(null, obj);
                            } catch (IllegalAccessException _) {
                                String objName = obj.getClass().getSimpleName();
                                String fieldName = field.getDeclaringClass().getSimpleName() + "#" + field.getName();
                                _logger.warning("An error occurred while injecting " + objName + " in " + fieldName);
                            }
                        });
                    });
        }
    }
}
