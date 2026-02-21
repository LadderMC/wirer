package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Snowtyy
 */
class WirerServiceProvider implements ServiceProvider {

    private final Map<Class<?>, Object> _singletonMap;

    private final Map<Class<?>, Object> _transientMap;

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
        Optional<Object> opt;

        opt = this.resolve(clazz);
        if(opt.isPresent())
            return opt;

        return this.finallyResolve(clazz);
    }

    Optional<Object> resolve(Class<?> clazz) {
        Optional<Object> opt;

        opt = this.resolve(clazz, _singletonMap, true);
        if(opt.isPresent())
            return opt;

        return this.resolve(clazz, _transientMap, false);
    }

    Optional<Object> finallyResolve(Class<?> clazz) {
        Optional<Object> opt;

        opt = this.finallyResolveInstance(clazz, _singletonMap);
        if(opt.isPresent())
            return opt;

        opt = this.finallyResolveClass(clazz, _singletonMap, true);
        if(opt.isPresent())
            return opt;

        return this.finallyResolveClass(clazz, _transientMap, false);
    }

    Optional<Object> resolve(Class<?> clazz, Map<Class<?>, Object> map, boolean persist) {
        if(!map.containsKey(clazz))
            return Optional.empty();

        Object value = map.get(clazz);
        if(value instanceof Class<?> impl) {
            Optional<Object> opt = map.values()
                    .stream()
                    .filter(o -> o.getClass() == impl)
                    .findFirst()
                    .or(() -> this.newInstance(impl));

            if(persist && opt.isPresent())
                map.put(clazz, opt.get());

            return opt;
        } else {
            return Optional.of(value);
        }
    }

    Optional<Object> finallyResolveInstance(Class<?> clazz, Map<Class<?>, Object> map) {
        return map.values()
                .stream()
                .filter(clazz::isInstance)
                .findFirst();
    }

    Optional<Object> finallyResolveClass(Class<?> clazz, Map<Class<?>, Object> map, boolean persist) {
        Optional<Object> opt = Optional.empty();
        for (var entry : map.entrySet()) {
            if (entry.getValue() instanceof Class<?> impl && clazz.isAssignableFrom(impl)) {
                opt = this.newInstance(impl);
                if(persist && opt.isPresent())
                    map.put(entry.getKey(), opt.get());
                break;
            }
        }

        return opt;
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

}
