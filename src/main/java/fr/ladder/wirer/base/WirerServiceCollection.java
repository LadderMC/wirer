package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;

import java.util.*;

/**
 * @author Snowtyy
 */
class WirerServiceCollection implements ServiceCollection {

    private final Map<Class<?>, Object> _singletonMap;

    private final Map<Class<?>, Class<?>> _transientMap;

    private final WirerServiceProvider _provider;

    public WirerServiceCollection() {
        _singletonMap = new HashMap<>();
        _transientMap = new HashMap<>();
        _provider = new WirerServiceProvider(this);
    }

    // region ---- Getters ----

    Map<Class<?>, Object> getSingletonMap() {
        return _singletonMap;
    }

    Map<Class<?>, Class<?>> getTransientMap() {
        return _transientMap;
    }

    // endregion

    // region ---- Singleton ----

    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass) {
        _singletonMap.put(iClass, tClass);
    }

    @Override
    public <T> void addSingleton(Class<T> tClass) {
        _singletonMap.put(tClass, tClass);
    }

    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, T impl) {
        _singletonMap.put(iClass, impl);
    }

    @Override
    public <T> void addSingleton(T impl) {
        _singletonMap.put(impl.getClass(), impl);
    }

    // endregion

    // region ---- Transient ----

    @Override
    public <I, T extends I> void addTransient(Class<I> iClass, Class<T> tClass) {
        _transientMap.put(iClass, tClass);
    }

    @Override
    public <T> void addTransient(Class<T> tClass) {
        _transientMap.put(tClass, tClass);
    }

    // endregion


    @Override
    public WirerServiceProvider toProvider() {
        return _provider;
    }
}
