package fr.ladder.wirer.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Snowtyy
 **/
public class WirerInjector {
    
    private final Map<Class<?>, Object> singletonMap;
    
    private final Map<Class<?>, Object> transientMap;
    
    public WirerInjector() {
        singletonMap = new ConcurrentHashMap<>();
        transientMap = new ConcurrentHashMap<>();
    }
    
}
