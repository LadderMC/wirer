package fr.ladder.wirer.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
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
    public void testAddSingletonClass1() {
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
    public void testAddSingletonClass2() {
        // act
        collection.addSingleton(Service.class);

        // assert
        Optional<IService> actual1 = collection.toProvider().get(IService.class);
        Optional<Service> actual2 = collection.toProvider().get(Service.class);

        assertTrue(actual1.isPresent());
        assertEquals(Service.class, actual1.get().getClass());

        assertTrue(actual2.isPresent());
        assertEquals(Service.class, actual2.get().getClass());

        assertSame(actual1.get(), actual2.get());
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

    @Test
    public void testResolveServiceProvider1() {
        String expected = "MyValue";
        // arrange
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(String.class, expected);

        WirerServiceProvider provider = collection.toProvider();

        // act
        var opt = provider.resolve(String.class, map);

        // assert
        assertTrue(opt.isPresent());
        assertEquals(expected, opt.get());
    }

    @Test
    public void testResolveServiceProvider2() {
        // arrange
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(IService.class, Service.class);

        WirerServiceProvider provider = collection.toProvider();

        // act
        var opt = provider.resolve(IService.class, map);

        // assert
        assertTrue(opt.isPresent());
        assertEquals(Service.class, opt.get().getClass());
    }

    @Test
    public void testResolveServiceProvider3() {
        // arrange
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(IService.class, Service.class);
        map.put(Service.class, Service.class);

        WirerServiceProvider provider = collection.toProvider();

        // act
        var opt1 = provider.resolve(Service.class, map);
        var opt2 = provider.resolve(IService.class, map);

        // assert
        assertTrue(opt1.isPresent());
        assertEquals(Service.class, opt1.get().getClass());

        assertTrue(opt2.isPresent());
        assertEquals(Service.class, opt2.get().getClass());

        assertSame(opt1.get(), opt2.get());
    }

    static class Service implements IService {
    }

    interface IService {

    }
}
