package fr.ladder.di.base;

import fr.ladder.di.ScopedServiceCollection;
import fr.ladder.di.ServiceCollection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Snowtyy
 */
public class WrapperServiceCollection implements ScopedServiceCollection {

    private final JavaPlugin _plugin;

    private final ServiceCollection _serviceCollection;

    public WrapperServiceCollection(JavaPlugin plugin, ServiceCollection serviceCollection) {
        _plugin = plugin;
        _serviceCollection = serviceCollection;
    }

    @Override
    public <I, Impl extends I> void addSingleton(Class<I> classInterface, Class<Impl> classImplementation) {
        _serviceCollection.addSingleton(classInterface, classImplementation);
    }

    @Override
    public <I, Impl extends I> void addSingleton(Class<I> classInterface, Impl implementation) {
        _serviceCollection.addSingleton(classInterface, implementation);
    }

    @Override
    public <I, Impl extends I> void addScoped(Class<I> classInterface, Class<Impl> classImplementation) {
        _serviceCollection.addScoped(_plugin, classInterface, classImplementation);
    }

    @Override
    public <I, Impl extends I> void addScoped(Class<I> classInterface, Impl implementation) {
        _serviceCollection.addScoped(_plugin, classInterface, implementation);
    }

    @Override
    public <Impl> void addScoped(Class<Impl> classImplementation) {
        _serviceCollection.addScoped(_plugin, classImplementation);
    }

    @Override
    public <Impl> void addScoped(Impl implementation) {
        _serviceCollection.addScoped(_plugin, implementation);
    }

    @Override
    public <I, Impl extends I> void addTransient(Class<I> classInterface, Class<Impl> classImplementation) {
        _serviceCollection.addTransient(classInterface, classImplementation);
    }
}
