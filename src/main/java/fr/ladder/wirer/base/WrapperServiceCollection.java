package fr.ladder.wirer.base;

import fr.ladder.wirer.ScopedServiceCollection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Snowtyy
 */
record WrapperServiceCollection(
        JavaPlugin _plugin,
        ServiceCollection _serviceCollection
) implements ScopedServiceCollection {

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
