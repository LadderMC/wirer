package fr.ladder.wirer.reflect;

import org.bukkit.plugin.java.JavaPlugin;

public interface IPluginInspectorHandler {

    IPluginInspector getInspector(JavaPlugin plugin);

}
