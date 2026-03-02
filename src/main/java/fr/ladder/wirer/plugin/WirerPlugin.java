package fr.ladder.wirer.plugin;

import fr.ladder.wirer.ServiceCollection;
import org.bukkit.plugin.Plugin;

/**
 * @author Snowtyy
 */
public interface WirerPlugin extends Plugin {

    void registerServices(ServiceCollection services);

}
