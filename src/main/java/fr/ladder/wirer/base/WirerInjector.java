package fr.ladder.wirer.base;

import fr.ladder.reflex.PluginInspector;
import fr.ladder.reflex.Reflex;
import fr.ladder.wirer.InjectedPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Snowtyy
 */
public class WirerInjector {

    private PluginManager _pluginManager;

    private Map<Plugin, PluginInspector> _inspectors;

    private WirerServiceCollection _serviceCollection;

    public WirerInjector() {
        _pluginManager = Bukkit.getPluginManager();
        _inspectors = new HashMap<>();
        _serviceCollection = new WirerServiceCollection();
    }

    public void injectAll() {
        if(_inspectors == null)
            throw new IllegalStateException("Injection has already been run.");

        // ============ SETUP PLUGINS ============
        for (Plugin plugin : _pluginManager.getPlugins()) {
            if(plugin.isEnabled() && plugin instanceof InjectedPlugin injectedPlugin) {
                this.setup(injectedPlugin);
            }
        }

        // ============ INJECTION ============
        _inspectors.forEach(_serviceCollection::injectAll);

        // ========= CLOSE INSPECTORS ===========
        _inspectors.forEach((plugin, inspector) -> inspector.close());

        // ============ FREE MEMORY ===========
        _pluginManager = null;
        _inspectors = null;
        _serviceCollection = null;
    }

    private void setup(InjectedPlugin plugin) {
        _inspectors.put(plugin, Reflex.getInspector(plugin));
        _serviceCollection.addAll(plugin, _inspectors.get(plugin));
        // default bindings
        _serviceCollection.addScoped(plugin, plugin);
        _serviceCollection.addScoped(plugin, plugin.getLogger());
        plugin.setup(new WrapperServiceCollection(plugin, _serviceCollection));
    }
}
