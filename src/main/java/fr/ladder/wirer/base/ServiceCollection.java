package fr.ladder.wirer.base;

import fr.ladder.wirer.reflect.PluginInspector;
import fr.ladder.wirer.annotation.Inject;
import fr.ladder.wirer.annotation.ToInject;
import fr.ladder.wirer.reflect.PluginInspectorHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class ServiceCollection {

    private final PluginInspectorHandler _inspectorHandler;

    private final Map<Class<?>, Object> _singletonMap;

    private final Map<ClassLoader, Map<Class<?>, Object>> _classLoaderMap;

    private final Map<Class<?>, Object> _transientMap;

    private final Set<Class<?>> _resolvingSet;

    ServiceCollection(PluginInspectorHandler inspectorHandler) {
        _inspectorHandler = inspectorHandler;
        _singletonMap = new HashMap<>();
        _classLoaderMap = new HashMap<>();
        _transientMap = new HashMap<>();
        _resolvingSet = new HashSet<>();
    }

    // region --- Singleton ---

        public <I, Impl extends I> void addSingleton(Class<I> classInterface, Class<Impl> classImplementation) {
        _singletonMap.put(classInterface, classImplementation);
    }

        public <I, Impl extends I> void addSingleton(Class<I> classInterface, Impl implementation) {
        _singletonMap.put(classInterface, implementation);
    }

    // endregion

    // region --- Scoped ---

    public <I, Impl extends I> void addScoped(JavaPlugin plugin, Class<I> classInterface, Class<Impl> classImplementation) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());
        _classLoaderMap.get(classLoader).put(classInterface, classImplementation);

    }

    public <I, Impl extends I> void addScoped(JavaPlugin plugin, Class<I> classInterface, Impl implementation) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());
        _classLoaderMap.get(classLoader).put(classInterface, implementation);
    }

    public <Impl> void addScoped(JavaPlugin plugin, Class<Impl> classImplementation) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());
        _classLoaderMap.get(classLoader).put(classImplementation, classImplementation);
    }

    public <Impl> void addScoped(JavaPlugin plugin, Impl implementation) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());
        _classLoaderMap.get(classLoader).put(implementation.getClass(), implementation);
    }

    // endregion

    // region --- Transient ---

    public <I, Impl extends I> void addTransient(Class<I> classInterface, Class<Impl> classImplementation) {
        _transientMap.put(classInterface, classImplementation);
    }

    // endregion

    public void addAll(JavaPlugin plugin) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());

        try(PluginInspector inspector = _inspectorHandler.getInspector(plugin)) {
            inspector.getClassesWithAnnotation(ToInject.class).forEach(classImplementation -> {
                ToInject toInject = classImplementation.getAnnotation(ToInject.class);
                Class<?> classInterface = toInject.value();
                if (classInterface.isAssignableFrom(classImplementation)) {
                    switch (toInject.type()) {
                        case SINGLETON -> _singletonMap.put(classInterface, classImplementation);
                        case SCOPED -> _classLoaderMap.get(classLoader).put(classInterface, classImplementation);
                        case TRANSIENT -> _transientMap.put(classInterface, classImplementation);
                    }
                }
            });
        }
    }


    public void injectAll(JavaPlugin plugin) {
        final var classLoader = plugin.getClass().getClassLoader();
        _classLoaderMap.putIfAbsent(classLoader, new HashMap<>());

        try(PluginInspector inspector = _inspectorHandler.getInspector(plugin)) {
            inspector.getFieldsWithAnnotation(Inject.class)
                    .filter(f -> Modifier.isPrivate(f.getModifiers()) && Modifier.isStatic(f.getModifiers()))
                    .forEach(field -> {
                        this.getInstance(classLoader, field.getType()).ifPresent(instance -> {
                            try {
                                field.setAccessible(true);
                                field.set(null, instance);
                            } catch (IllegalAccessException ignored) {
                            }
                        });
                    });
        }
    }

    private Optional<Object> getInstance(ClassLoader classLoader, Class<?> classInterface) {

        Optional<Object> result;

        // Fetch instance from the singleton map
        result = this.getInstanceFromMap(_singletonMap, classLoader, classInterface, true);
        if (result.isPresent()) {
            return result;
        }

        var scopedMap = _classLoaderMap.get(classLoader);
        // Fetch instance from the scoped map
        result = this.getInstanceFromMap(scopedMap, classLoader, classInterface, true);
        if (result.isPresent()) {
            return result;
        }

        // Fetch instance from the transient map
        result = this.getInstanceFromMap(_transientMap, classLoader, classInterface, false);
        if (result.isPresent()) {
            return result;
        }

        throw new IllegalStateException("Class: '" + classInterface.getName() + "' has been not found!");
    }

    private Optional<Object> getInstanceFromMap(Map<Class<?>, Object> objectMap, ClassLoader classLoader, Class<?> classInterface, boolean persist) {
        if (objectMap.containsKey(classInterface)) {
            Object object = objectMap.get(classInterface);
            if (object instanceof Class<?> classImplementation) {
                var instance = this.newInstance(classLoader, classImplementation);
                if (persist) {
                    instance.ifPresent(o -> objectMap.put(classInterface, o));
                }
                return instance;
            } else {
                return Optional.of(object);
            }
        }

        return Optional.empty();
    }

    private Optional<Object> newInstance(ClassLoader classLoader, Class<?> classImplementation) {
        if (_resolvingSet.contains(classImplementation)) {
            throw new IllegalStateException("A circular dependency has been detected for the class: '" + classImplementation.getName() + "'");
        }

        try {
            _resolvingSet.add(classImplementation);
            var constructors = classImplementation.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new IllegalStateException("The class: '" + classImplementation.getName() + "' must have exactly one constructor.");
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
                parameters[i] = this.getInstance(classLoader, paramType);
            }

            return Optional.of(constructor.newInstance(parameters));
        } catch (InstantiationException | IllegalAccessException |
                 IllegalArgumentException | InvocationTargetException ignored) {
            throw new IllegalStateException("An error occurred while trying to instantiate the class: '" + classImplementation.getName() + "'");
        } finally {
            _resolvingSet.remove(classImplementation);
        }
    }

}
