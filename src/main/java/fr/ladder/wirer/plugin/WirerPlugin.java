package fr.ladder.wirer.plugin;

import fr.ladder.wirer.ScopedServiceCollection;
import org.bukkit.plugin.Plugin;

/**
 * @author Snowtyy
 */
public interface WirerPlugin extends Plugin {

    void registerServices(ScopedServiceCollection services);

}
