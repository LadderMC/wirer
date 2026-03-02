package fr.ladder.wirer.base;

import fr.ladder.wirer.ServiceCollection;
import fr.ladder.wirer.ServiceProvider;
import fr.ladder.wirer.base.mock.IService;
import fr.ladder.wirer.base.mock.ITransient;
import fr.ladder.wirer.base.mock.Service;
import fr.ladder.wirer.base.mock.Transient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Snowtyy
 **/
public class ServiceProviderTest {

    private static ServiceProvider newProviderWith(Consumer<ServiceCollection> consumer) {
        Map<Class<?>, Object> singletonMap = new HashMap<>();
        Map<Class<?>, Object> transientMap = new HashMap<>();
        Map<Class<?>, Object> scopedMap = new HashMap<>();
        WirerServiceContainer container = new WirerServiceContainer(singletonMap, transientMap, scopedMap);

        consumer.accept(container);
        return container;
    }

    @Test
    public void testGetSingleton1() {
        String expected = "singleton value";
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addSingleton(expected);
        });

        // act
        Optional<String> actual = serviceProvider.get(String.class);

        // assert
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void testGetSingleton2() {
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addSingleton(IService.class, Service.class);
        });

        // act
        Optional<IService> actual1 = serviceProvider.get(IService.class);
        Optional<IService> actual2 = serviceProvider.get(IService.class);

        // assert
        assertTrue(actual1.isPresent());
        assertTrue(actual2.isPresent());
        assertSame(actual1.get(), actual2.get());
    }

    @Test
    public void testGetSingleton3() {
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addSingleton("singleton value");
            serviceCollection.addSingleton(IService.class, Service.class);
            serviceCollection.addTransient(ITransient.class, Transient.class);
        });

        // act
        Optional<IService> actual1 = serviceProvider.get(IService.class);
        Optional<Service> actual2 = serviceProvider.get(Service.class);

        // assert
        assertTrue(actual1.isPresent());
        assertTrue(actual2.isPresent());
        assertSame(actual1.get(), actual2.get());
    }

    @Test
    public void testGetTransient1() {
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addTransient(ITransient.class, Transient.class);
        });

        // act
        Optional<ITransient> actual1 = serviceProvider.get(ITransient.class);
        Optional<ITransient> actual2 = serviceProvider.get(ITransient.class);

        // assert
        assertTrue(actual1.isPresent());
        assertTrue(actual2.isPresent());
        assertNotSame(actual1.get(), actual2.get());
    }

    @Test
    public void testGetScoped1() {
        String expected = "scoped value";
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addScoped(expected);
        });

        // act
        Optional<String> actual = serviceProvider.get(String.class);

        // assert
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void testGetScoped2() {
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addScoped(IService.class, Service.class);
        });

        // act
        Optional<IService> actual1 = serviceProvider.get(IService.class);
        Optional<IService> actual2 = serviceProvider.get(IService.class);

        // assert
        assertTrue(actual1.isPresent());
        assertTrue(actual2.isPresent());
        assertSame(actual1.get(), actual2.get());
    }

    @Test
    public void testGetScoped3() {
        // arrange
        ServiceProvider serviceProvider = newProviderWith(serviceCollection -> {
            serviceCollection.addSingleton("singleton value");
            serviceCollection.addScoped(IService.class, Service.class);
            serviceCollection.addTransient(ITransient.class, Transient.class);
        });

        // act
        Optional<IService> actual1 = serviceProvider.get(IService.class);
        Optional<Service> actual2 = serviceProvider.get(Service.class);

        // assert
        assertTrue(actual1.isPresent());
        assertTrue(actual2.isPresent());
        assertSame(actual1.get(), actual2.get());
    }

}
