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
        return _parent.resolve(clazz, _scopedMap)
                .or(() -> _parent.getInstance(clazz));
    }

}
