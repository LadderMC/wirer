package fr.ladder.wirer.base;

import fr.ladder.wirer.ScopedServiceCollection;

import java.util.*;

/**
 * @author Snowtyy
 */
class WirerScopedServiceCollection implements ScopedServiceCollection {

    private final WirerServiceCollection _parent;

    private final Map<Class<?>, Object> _scopedMap;

    private final WirerScopedServiceProvider _provider;

    public WirerScopedServiceCollection(WirerServiceCollection parent) {
        _parent = parent;
        _scopedMap = new HashMap<>();
        _provider = new WirerScopedServiceProvider(this);
    }

    // region ---- Getters ----

    WirerServiceCollection getParent() {
        return _parent;
    }

    Map<Class<?>, Object> getScopedMap() {
        return _scopedMap;
    }

    // endregion

    // region ---- Singleton ----

    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass) {
        _parent.addSingleton(iClass, tClass);
    }

    @Override
    public <T> void addSingleton(Class<T> tClass) {
        _parent.addSingleton(tClass);
    }

    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, T impl) {
        _parent.addSingleton(iClass, impl);
    }

    @Override
    public <T> void addSingleton(T impl) {
        _parent.addSingleton(impl);
    }

    // endregion

    // region ---- Transient ----

    @Override
    public <I, T extends I> void addTransient(Class<I> iClass, Class<T> tClass) {
        _parent.addTransient(iClass, tClass);
    }

    @Override
    public <T> void addTransient(Class<T> tClass) {
        _parent.addTransient(tClass);
    }

    // endregion

    // region ---- Scoped ----

    @Override
    public <I, T extends I> void addScoped(Class<I> iClass, Class<T> tClass) {
        _scopedMap.put(iClass, tClass);
    }

    @Override
    public <T> void addScoped(Class<T> tClass) {
        _scopedMap.put(tClass, tClass);
    }

    @Override
    public <I, T extends I> void addScoped(Class<I> iClass, T impl) {
        _scopedMap.put(iClass, impl);
    }

    @Override
    public <T> void addScoped(T impl) {
        _scopedMap.put(impl.getClass(), impl);
    }

    // endregion

    @Override
    public WirerScopedServiceProvider toProvider() {
        return _provider;
    }
}
