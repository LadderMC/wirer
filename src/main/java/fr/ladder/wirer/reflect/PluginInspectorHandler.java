package fr.ladder.wirer.reflect;

import org.bukkit.plugin.java.JavaPlugin;

public interface PluginInspectorHandler {

    PluginInspector getInspector(JavaPlugin plugin);

}
