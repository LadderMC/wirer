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
            if(!_transientMap.containsKey(clazz))
                return Optional.empty();

            return this.newInstance(_transientMap.get(clazz));
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
        Optional<Object> opt;

        if(map.containsKey(clazz)) {
            Object value = map.get(clazz);
            if(value instanceof Class<?> impl) {
                opt = map.values()
                        .stream()
                        .filter(o -> o.getClass() == impl)
                        .findFirst()
                        .or(() -> this.newInstance(impl));

                opt.ifPresent(o -> map.put(clazz, o));

                return opt;
            } else {
                // return directly because it isn't needed to put update map
                return Optional.of(value);
            }
        }

        // get service instance
        opt = _singletonMap.values()
                .stream()
                .filter(clazz::isInstance)
                .findFirst();

        if(opt.isPresent())
            return opt;

        // last chance try to get instanced service to initialize
        for (var entry : map.entrySet()) {
            if(entry.getValue() instanceof Class<?> impl && clazz.isAssignableFrom(impl)) {
                opt = this.newInstance(impl);
                opt.ifPresent(o -> map.put(entry.getKey(), o));
                break;
            }
        }

        return opt;
    }

}
