package fr.ladder.wirer;

/**
 * @author Snowtyy
 */
public interface ServiceCollection {

 <I, Impl extends I> void addSingleton(Class<I> classInterface, Class<Impl> classImplementation);

 <I, Impl extends I> void addSingleton(Class<I> classInterface, Impl implementation);

 <I, Impl extends I> void addScoped(Class<I> classInterface, Class<Impl> classImplementation);

 <I, Impl extends I> void addScoped(Class<I> classInterface, Impl implementation);

 <Impl> void addScoped(Class<Impl> classImplementation);

 <Impl> void addScoped(Impl implementation);

 <I, Impl extends I> void addTransient(Class<I> classInterface, Class<Impl> classImplementation);

}
