package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Snowtyy
 */
class WirerServiceProvider implements ServiceProvider {

    private final Map<Class<?>, Object> _singletonMap;

    private final Map<Class<?>, Class<?>> _transientMap;

    private final Set<Object> _resolvingSet;

    public WirerServiceProvider(WirerServiceCollection serviceCollection) {
        _singletonMap = serviceCollection.getSingletonMap();
        _transientMap = serviceCollection.getTransientMap();
        _resolvingSet = new HashSet<>();
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz) {
        return this.getInstance(clazz)
                .map(clazz::cast);
    }

    Optional<Object> getInstance(Class<?> clazz) {
        return this.resolve(clazz, _singletonMap).or(() -> {
            if(_transientMap.containsKey(clazz)) {
                return this.newInstance(_transientMap.get(clazz));
            }

            return _singletonMap.values()
                    .stream()
                    .filter(clazz::isInstance)
                    .findFirst();
        });
    }

    Optional<Object> newInstance(Class<?> clazz) {
        if (_resolvingSet.contains(clazz)) {
            throw new IllegalStateException("A circular dependency has been detected for the class: '" + clazz.getName() + "'");
        }

        try {
            _resolvingSet.add(clazz);
            var constructors = clazz.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new IllegalStateException("The class: '" + clazz.getName() + "' must have exactly one constructor.");
            }

            var constructor = constructors[0];

            // try to instance without parameters
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor.newInstance());
            }

            var paramTypes = constructor.getParameterTypes();
            Object[] parameters = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                var opt = this.getInstance(paramType);
                if(opt.isPresent()) {
                    parameters[i] = opt.get();
                }
            }

            return Optional.of(constructor.newInstance(parameters));
        } catch (InstantiationException | IllegalAccessException |
                 IllegalArgumentException | InvocationTargetException ignored) {
            throw new IllegalStateException("An error occurred while trying to instantiate the class: '" + clazz.getName() + "'");
        } finally {
            _resolvingSet.remove(clazz);
        }
    }

    Optional<Object> resolve(Class<?> clazz, Map<Class<?>, Object> map) {
        if(!map.containsKey(clazz))
            return Optional.empty();

        Object value = map.get(clazz);
        if(!(value instanceof Class<?> impl))
            return Optional.of(value);

        Optional<Object> opt = map.values()
                .stream()
                .filter(obj -> impl.equals(obj.getClass()))
                .findFirst()
                .or(() -> this.newInstance(impl));

        opt.ifPresent(obj -> map.put(clazz, obj));

        return opt;
    }

}
