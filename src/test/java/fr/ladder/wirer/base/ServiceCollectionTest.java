package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;
import fr.ladder.wirer.base.mock.*;
import fr.ladder.wirer.exception.NotInstantiableException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);
        
        // act
        serviceCollection.addSingleton(expected);
        Object actual = singletonMap.get(String.class);
        
        // assert
        assertEquals(1, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(0, scopedMap.size());
        assertEquals(expected, actual);
    }
    
    @Test
    public void testAddSingleton2() {
        IService expected = new Service();
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);
        
        // act
        serviceCollection.addSingleton(IService.class, expected);
        Object actual = singletonMap.get(IService.class);
        
        // assert
        assertEquals(1, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(0, scopedMap.size());
        assertSame(expected, actual);
    }

    @Test
    public void testAddSingleton3() {
        Class<Service> expected = Service.class;
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);
        
        // act
        serviceCollection.addSingleton(IService.class, expected);
        Object actual = singletonMap.get(IService.class);
        
        // assert
        assertEquals(1, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(0, scopedMap.size());
        assertSame(expected, actual);
    }
    
    @Test
    public void testAddTransient() {
        Class<Transient> expected = Transient.class;
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);
        
        // act
        serviceCollection.addTransient(ITransient.class, expected);
        Object actual = transientMap.get(ITransient.class);
        
        // assert
        assertEquals(0, singletonMap.size());
        assertEquals(1, transientMap.size());
        assertEquals(0, scopedMap.size());
        assertSame(expected, actual);
    }

    @Test
    public void testAddScoped1() {
        String expected = "scoped value";
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);

        // act
        serviceCollection.addScoped(expected);
        Object actual = scopedMap.get(String.class);

        // assert
        assertEquals(0, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(1, scopedMap.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testAddScoped2() {
        IService expected = new Service();
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);

        // act
        serviceCollection.addScoped(IService.class, expected);
        Object actual = scopedMap.get(IService.class);

        // assert
        assertEquals(0, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(1, scopedMap.size());
        assertSame(expected, actual);
    }

    @Test
    public void testAddScoped3() {
        Class<Service> expected = Service.class;
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);

        // act
        serviceCollection.addScoped(IService.class, expected);
        Object actual = scopedMap.get(IService.class);

        // assert
        assertEquals(0, singletonMap.size());
        assertEquals(0, transientMap.size());
        assertEquals(1, scopedMap.size());
        assertSame(expected, actual);
    }

    @Test
    public void testAddNotInstiantiableClass() {
        // arrange
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        ServiceCollection serviceCollection = new WirerServiceContainer(singletonMap, transientMap, scopedMap);

        // act + assert
        assertThrows(NotInstantiableException.class, () -> serviceCollection.addSingleton(IService.class));
        assertThrows(NotInstantiableException.class, () -> serviceCollection.addSingleton(AbstractService.class));

        assertThrows(NotInstantiableException.class, () -> serviceCollection.addTransient(ITransient.class));
        assertThrows(NotInstantiableException.class, () -> serviceCollection.addTransient(AbstractTransient.class));

        assertThrows(NotInstantiableException.class, () -> serviceCollection.addScoped(IService.class));
        assertThrows(NotInstantiableException.class, () -> serviceCollection.addScoped(AbstractService.class));

    }
}
