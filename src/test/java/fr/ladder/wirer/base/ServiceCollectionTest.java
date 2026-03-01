package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;
import fr.ladder.wirer.base.mock.IService;
import fr.ladder.wirer.base.mock.ITransient;
import fr.ladder.wirer.base.mock.Service;
import fr.ladder.wirer.base.mock.Transient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Snowtyy
 **/
public class ServiceCollectionTest {

    @Test
    public void testAddSingleton1() {
        String expected = "singleton value";
        // arrange
        Map<Class<?>, Object> singletons = new HashMap<>();
        Map<Class<?>, Object> transients = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletons, transients);
        
        // act
        serviceCollection.addSingleton(expected);
        Object actual = singletons.get(String.class);
        
        // assert
        assertEquals(1, singletons.size());
        assertEquals(0, transients.size());
        assertEquals(expected, actual);
    }
    
    @Test
    public void testAddSingleton2() {
        IService expected = new Service();
        // arrange
        Map<Class<?>, Object> singletons = new HashMap<>();
        Map<Class<?>, Object> transients = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletons, transients);
        
        // act
        serviceCollection.addSingleton(IService.class, expected);
        Object actual = singletons.get(IService.class);
        
        // assert
        assertEquals(1, singletons.size());
        assertEquals(0, transients.size());
        assertSame(expected, actual);
    }

    @Test
    public void testAddSingleton3() {
        Class<Service> expected = Service.class;
        // arrange
        Map<Class<?>, Object> singletons = new HashMap<>();
        Map<Class<?>, Object> transients = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletons, transients);
        
        // act
        serviceCollection.addSingleton(IService.class, expected);
        Object actual = singletons.get(IService.class);
        
        // assert
        assertEquals(1, singletons.size());
        assertEquals(0, transients.size());
        assertSame(expected, actual);
    }
    
    @Test
    public void testAddTransient1() {
        Class<Transient> expected = Transient.class;
        // arrange
        Map<Class<?>, Object> singletons = new HashMap<>();
        Map<Class<?>, Object> transients = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletons, transients);
        
        // act
        serviceCollection.addTransient(ITransient.class, expected);
        Object actual = transients.get(ITransient.class);
        
        // assert
        assertEquals(0, singletons.size());
        assertEquals(1, transients.size());
        assertSame(expected, actual);
    }
    
    public static class WirerServiceContainerStub {
    
    
    
    }
}
