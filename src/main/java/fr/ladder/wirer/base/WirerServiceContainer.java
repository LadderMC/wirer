package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceProvider;
import io.netty.util.internal.ConcurrentSet;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Snowtyy
 **/
public class WirerServiceContainer extends AbstractServiceCollection implements ServiceProvider {
   
    private final Map<Class<?>, Object> singletonMap;
    
    private final Map<Class<?>, Object> transientMap;
    
    private final Map<Class<?>, Object> scopedMap;
    
    private final Set<Class<?>> resolvingSet;
    
    public WirerServiceContainer(Map<Class<?>, Object> singletons, Map<Class<?>, Object> transients) {
        singletonMap = singletons;
        transientMap = transients;
        scopedMap = new ConcurrentHashMap<>();
        resolvingSet = new ConcurrentSet<>();
    }
    
    @Override
    protected void registerSingleton(Class<?> clazz, Object impl) {
        singletonMap.put(clazz, impl);
    }
    
    @Override
    protected void registerScoped(Class<?> clazz, Object impl) {
        scopedMap.put(clazz, impl);
    }
    
    @Override
    protected void registerTransient(Class<?> clazz, Class<?> impl) {
        transientMap.put(clazz, impl);
    }
    
    @Override
    public <T> Optional<T> get(Class<T> clazz) {
        return this.getInstance(clazz).map(clazz::cast);
    }
    
    protected synchronized Optional<?> newInstance(Class<?> clazz) {
        if (resolvingSet.contains(clazz)) {
            throw new IllegalStateException("A circular dependency has been detected for the class: '" + clazz.getName() + "'");
        }
        
        try {
            resolvingSet.add(clazz);
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
                Optional<?> opt = this.getInstance(paramType);
                if(opt.isPresent()) {
                    parameters[i] = opt.get();
                }
            }
            
            return Optional.of(constructor.newInstance(parameters));
        } catch (InstantiationException | IllegalAccessException |
                 IllegalArgumentException | InvocationTargetException ignored) {
            throw new IllegalStateException("An error occurred while trying to instantiate the class: '" + clazz.getName() + "'");
        } finally {
            resolvingSet.remove(clazz);
        }
    }
    
    protected synchronized Optional<?> getInstance(Class<?> clazz) {
        Optional<?> opt;
        
        opt = this.resolveInstance(clazz);
        if (opt.isPresent())
            return opt;
        
        return this.finallyResolveInstance(clazz);
    }
    
    protected synchronized Optional<?> resolveInstance(Class<?> clazz) {
        Optional<?> opt;
        
        opt = this.resolveInstance(clazz, scopedMap, true);
        if (opt.isPresent())
            return opt;
        
        opt = this.resolveInstance(clazz, singletonMap, true);
        if(opt.isPresent())
            return opt;
        
        return this.resolveInstance(clazz, transientMap, false);
    }
    
    protected synchronized Optional<?> finallyResolveInstance(Class<?> clazz) {
        Optional<?> opt;
        
        opt = this.finallyResolveInstance(clazz, scopedMap);
        if(opt.isPresent())
            return opt;
        
        opt = this.finallyResolveClass(clazz, scopedMap, true);
        if(opt.isPresent())
            return opt;
        
        opt = this.finallyResolveInstance(clazz, singletonMap);
        if(opt.isPresent())
            return opt;
        
        opt = this.finallyResolveClass(clazz, singletonMap, true);
        if(opt.isPresent())
            return opt;
        
        return this.finallyResolveClass(clazz, transientMap, false);
    }
    
    private Optional<?> resolveInstance(Class<?> clazz, Map<Class<?>, Object> map, boolean persist) {
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
    
    private Optional<?> finallyResolveInstance(Class<?> clazz, Map<Class<?>, Object> map) {
        return map.values()
                .stream()
                .filter(clazz::isInstance)
                .findFirst();
    }
    
    private Optional<?> finallyResolveClass(Class<?> clazz, Map<Class<?>, Object> map, boolean persist) {
        Optional<?> opt = Optional.empty();
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
    
    
}
