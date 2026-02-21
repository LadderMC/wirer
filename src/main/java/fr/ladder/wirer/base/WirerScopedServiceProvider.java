package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceProvider;

import java.util.*;

/**
 * @author Snowtyy
 */
class WirerScopedServiceProvider implements ServiceProvider {

    private final WirerServiceProvider _parent;

    private final Map<Class<?>, Object> _scopedMap;

    public WirerScopedServiceProvider(WirerScopedServiceCollection serviceCollection) {
        _parent = serviceCollection.getParent().toProvider();
        _scopedMap = serviceCollection.getScopedMap();
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz) {
        return this.getInstance(clazz)
                .map(clazz::cast);
    }

    Optional<Object> getInstance(Class<?> clazz) {
        Optional<Object> opt;

        // resolve scoped instance
        opt = _parent.resolve(clazz, _scopedMap, true);
        if(opt.isPresent())
            return opt;

        // resolve singleton or transient instance
        opt = _parent.resolve(clazz);
        if(opt.isPresent())
            return opt;

        // finally try to resolve scoped instance
        opt = _parent.finallyResolveInstance(clazz, _scopedMap);
        if(opt.isPresent())
            return opt;

        // finally try to resolve scoped class
        opt = _parent.finallyResolveClass(clazz, _scopedMap, true);
        if(opt.isPresent())
            return opt;

        // finally try to resolve singleton or transient instance or class.
        return _parent.finallyResolve(clazz);
    }
}
