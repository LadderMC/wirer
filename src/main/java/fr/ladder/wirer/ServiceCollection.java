package fr.ladder.wirer;

/**
 * @author Snowtyy
 */
public interface ServiceCollection {

    // region ====== Singleton ======
    
    <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass);

    <T> void addSingleton(Class<T> tClass);

    <I, T extends I> void addSingleton(Class<I> iClass, T impl);

    <T> void addSingleton(T impl);

    // endregion
    
    // region ====== Scoped ======
    
    <I, T extends I> void addScoped(Class<I> iClass, Class<T> tClass);
    
    <T> void addScoped(Class<T> tClass);
    
    <I, T extends I> void addScoped(Class<I> iClass, T impl);
    
    <T> void addScoped(T impl);
    
    // endregion
    
    // region ====== Transient ======
    
    <I, T extends I> void addTransient(Class<I> iClass, Class<T> tClass);

    <T> void addTransient(Class<T> tClass);
    
    // endregion
}
