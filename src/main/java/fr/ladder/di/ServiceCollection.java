package fr.ladder.di;

import org.bukkit.plugin.java.JavaPlugin;

public interface ServiceCollection {

    <I, Impl extends I> void addSingleton(Class<I> classInterface, Class<Impl> classImplementation);

    <I, Impl extends I> void addSingleton(Class<I> classInterface, Impl implementation);

    <I, Impl extends I> void addScoped(JavaPlugin plugin, Class<I> classInterface, Class<Impl> classImplementation);

    <I, Impl extends I> void addScoped(JavaPlugin plugin, Class<I> classInterface, Impl implementation);

    <Impl> void addScoped(JavaPlugin plugin, Class<Impl> classImplementation);

    <Impl> void addScoped(JavaPlugin plugin, Impl implementation);

    <I, Impl extends I> void addTransient(Class<I> classInterface, Class<Impl> classImplementation);

}
