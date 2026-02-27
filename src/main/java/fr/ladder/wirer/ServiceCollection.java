package fr.ladder.wirer;

/**
 * @author Snowtyy
 */
public interface ServiceCollection {

    <I, T extends I> void addSingleton(Class<I> iClass, Class<T> tClass);

    <T> void addSingleton(Class<T> tClass);

    <I, T extends I> void addSingleton(Class<I> iClass, T impl);

    <T> void addSingleton(T impl);

    <I, T extends I> void addTransient(Class<I> iClass, Class<T> tClass);

    <T> void addTransient(Class<T> tClass);
}
