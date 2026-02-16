package fr.ladder.wirer;

/**
 * @author Snowtyy
 */
public interface ScopedServiceCollection extends ServiceCollection {

    <I, T extends I> void addScoped(Class<I> iClass, Class<T> tClass);

    <T> void addScoped(Class<T> tClass);

    <I, T extends I> void addScoped(Class<I> iClass, T impl);

    <T> void addScoped(T impl);

}
