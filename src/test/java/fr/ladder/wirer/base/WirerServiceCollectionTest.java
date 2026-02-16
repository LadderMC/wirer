package fr.ladder.wirer.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WirerServiceCollectionTest {

    private WirerServiceCollection collection;

    @BeforeEach
    public void setup() {
        collection = new WirerServiceCollection();
    }

    @Test
    void testAddSingletonInstance() {
        String expected = "MyValue";

        // act
        collection.addSingleton(String.class, expected);

        // assert
        Optional<String> actual = collection.toProvider()
                .get(String.class);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void testAddSingletonClass() {
        // act
        collection.addSingleton(IService.class, Service.class);

        // assert
        Optional<IService> actual = collection.toProvider()
                .get(IService.class);

        assertTrue(actual.isPresent());
        assertEquals(Service.class, actual.get().getClass());

        Optional<IService> another = collection.toProvider()
                .get(IService.class);

        assertTrue(another.isPresent());
        assertSame(actual.get(), another.get());
    }

    @Test
    void testAddTransientClass() {
        // act
        collection.addTransient(IService.class, Service.class);

        // assert
        Optional<IService> actual = collection.toProvider()
                .get(IService.class);

        assertTrue(actual.isPresent());
        assertEquals(Service.class, actual.get().getClass());

        Optional<IService> another = collection.toProvider()
                .get(IService.class);

        assertTrue(another.isPresent());
        assertNotSame(actual.get(), another.get());
    }

    @Test
    public void testAddSingletonClasses() {
        // act
        collection.addSingleton(IService.class, Service.class);
        collection.addSingleton(Service.class, Service.class);

        // assert
        Optional<IService> service1 = collection.toProvider()
                .get(IService.class);

        Optional<Service> service2 = collection.toProvider()
                .get(Service.class);

        assertTrue(service1.isPresent());
        assertTrue(service2.isPresent());

        assertSame(service1.get(), service2.get());
    }

    @Test
    public void testAddSingletonInstances() {
        Service expected = new Service();
        // act
        collection.addSingleton(IService.class, expected);
        collection.addSingleton(Service.class, Service.class);

        // assert
        Optional<IService> service1 = collection.toProvider()
                .get(IService.class);

        Optional<Service> service2 = collection.toProvider()
                .get(Service.class);

        assertTrue(service1.isPresent());
        assertTrue(service2.isPresent());

        assertSame(expected, service1.get());
        assertSame(expected, service2.get());
    }

    static class Service implements IService {
    }

    interface IService {

    }
}
