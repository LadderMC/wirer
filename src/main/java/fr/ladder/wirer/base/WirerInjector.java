package fr.ladder.wirer.base;

import fr.ladder.wirer.plugin.WirerPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Snowtyy
 **/
public class WirerInjector {
    
    private final Map<Class<?>, Object> singletonMap;
    
    private final Map<Class<?>, Object> transientMap;
    
    private final Map<WirerPlugin, WirerServiceContainer> serviceContainerMap;
    
    public WirerInjector() {
        singletonMap = new ConcurrentHashMap<>();
        transientMap = new ConcurrentHashMap<>();
        serviceContainerMap = new HashMap<>();
    }
    
    public void initialize() {
    
    }
    
}
