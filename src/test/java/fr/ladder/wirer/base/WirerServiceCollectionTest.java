package fr.ladder.wirer.base;

import fr.ladder.wirer.base.mock.*;

import fr.ladder.wirer.exception.NotInstantiableException;
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
    public void testAddInstantiable() {
        // act
        collection.addSingleton(IService.class, Service.class);
        collection.addSingleton(Service.class);

        collection.addTransient(ITransient.class, Transient.class);
        collection.addTransient(Transient.class);
    }

    @Test
    public void testAddNotInstantiable() {
        // assert
        assertThrows(NotInstantiableException.class, () ->
                collection.addSingleton(IService.class, IService.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addSingleton(IService.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addSingleton(IService.class, AbstractService.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addSingleton(AbstractService.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addTransient(ITransient.class, ITransient.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addTransient(ITransient.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addTransient(ITransient.class, AbstractTransient.class));

        assertThrows(NotInstantiableException.class, () ->
                collection.addTransient(AbstractTransient.class));
    }

    @Test
    public void testAddSingletonInstance() {
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
}
