package fr.ladder.wirer;

import org.bukkit.plugin.Plugin;

/**
 * @author Snowtyy
 */
public interface InjectedPlugin extends Plugin {

    void setup(ServiceCollection services);

}
