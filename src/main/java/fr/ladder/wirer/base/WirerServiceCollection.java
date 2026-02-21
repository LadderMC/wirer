package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;
import fr.ladder.wirer.exception.NotInstantiableException;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Snowtyy
 */
class WirerServiceCollection implements ServiceCollection {

    private final Map<Class<?>, Object> _singletonMap;

    private final Map<Class<?>, Object> _transientMap;

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

    Map<Class<?>, Object> getTransientMap() {
        return _transientMap;
    }

    // endregion

    // region ---- Singleton ----

    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass) {
        if(isInstantiable(tClass)) {
            _singletonMap.put(iClass, tClass);
        } else {
            throw new NotInstantiableException(tClass);
        }
    }

    @Override
    public <T> void addSingleton(Class<T> tClass) {
        if(isInstantiable(tClass)) {
            _singletonMap.put(tClass, tClass);
        } else {
            throw new NotInstantiableException(tClass);
        }
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
        if(isInstantiable(tClass)) {
            _transientMap.put(iClass, tClass);
        } else {
            throw new NotInstantiableException(tClass);
        }
    }

    @Override
    public <T> void addTransient(Class<T> tClass) {
        if(isInstantiable(tClass)) {
            _transientMap.put(tClass, tClass);
        } else {
            throw new NotInstantiableException(tClass);
        }
    }

    // endregion

    static boolean isInstantiable(Class<?> clazz) {
        int modifiers = clazz.getModifiers();

        return !clazz.isInterface()                // Pas une interface
                && !Modifier.isAbstract(modifiers)     // Pas une classe abstraite
                && !clazz.isPrimitive()                // Pas un type primitif (int, double, etc.)
                && !clazz.isArray();                   // Pas un tableau
    }

    @Override
    public WirerServiceProvider toProvider() {
        return _provider;
    }

}
