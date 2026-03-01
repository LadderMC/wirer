package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;

/**
 * @author Snowtyy
 **/
public abstract class AbstractServiceCollection implements ServiceCollection {
    
    // region ====== Singleton ======
    
    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerSingleton(iClass, tClass);
    }
    
    @Override
    public <T> void addSingleton(Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerSingleton(tClass, tClass);
    }
    
    @Override
    public <I, T extends I> void addSingleton(Class<I> iClass, T impl) {
        this.registerSingleton(iClass, impl);
    }
    
    @Override
    public <T> void addSingleton(T impl) {
        this.registerSingleton(impl.getClass(), impl);
    }
    
    // endregion
    
    // region ====== Scoped ======
    
    @Override
    public <I, T extends I> void addScoped(Class<I> iClass, Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerSingleton(iClass, tClass);
    
    }
    
    @Override
    public <T> void addScoped(Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerSingleton(tClass, tClass);
    }
    
    @Override
    public <I, T extends I> void addScoped(Class<I> iClass, T impl) {
        this.registerSingleton(iClass, impl);
    }
    
    @Override
    public <T> void addScoped(T impl) {
        this.registerScoped(impl.getClass(), impl);
    }
    
    // endregion
    
    // region ====== Transient ======
    
    @Override
    public <I, T extends I> void addTransient(Class<I> iClass, Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerTransient(iClass, tClass);
    }
    
    @Override
    public <T> void addTransient(Class<T> tClass) {
        WirerUtils.ensureInstantiable(tClass);
        this.registerTransient(tClass, tClass);
    }
    
    // endregion
    
    protected abstract void registerSingleton(Class<?> clazz, Object impl);
    
    protected abstract void registerScoped(Class<?> clazz, Object impl);
    
    protected abstract void registerTransient(Class<?> clazz, Class<?> impl);
}
