package fr.ladder.wirer.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WirerScopedServiceCollectionTest {

    private WirerScopedServiceCollection collection;

    @BeforeEach
    public void setup() {
        collection = new WirerScopedServiceCollection(new WirerServiceCollection());
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
    public void testAddTransientClass() {
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
    public void testAddScopedInstance() {
        String expected = "MyValue";

        // act
        collection.addScoped(String.class, expected);

        // assert
        Optional<String> actual = collection.toProvider()
                .get(String.class);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    public void testAddScopedClass() {
        // act
        collection.addScoped(IService.class, Service.class);

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
    public void testAddSingletonWithScopes1() {
        String expected = "MyValue";

        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        parent.addSingleton(String.class, expected);

        // assert

        Optional<String> actualScope1 = scope1.toProvider().get(String.class);
        Optional<String> actualScope2 = scope2.toProvider().get(String.class);

        assertTrue(actualScope1.isPresent());
        assertTrue(actualScope2.isPresent());

        assertSame(expected, actualScope1.get());
        assertSame(expected, actualScope2.get());
    }

    @Test
    public void testAddSingletonWithScopes2() {
        String expected = "MyValue";

        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope1.addSingleton(String.class, expected);

        // assert

        Optional<String> actualParent = parent.toProvider().get(String.class);
        Optional<String> actualScope2 = scope2.toProvider().get(String.class);

        assertTrue(actualParent.isPresent());
        assertTrue(actualScope2.isPresent());

        assertSame(expected, actualParent.get());
        assertSame(expected, actualScope2.get());
    }

    @Test
    public void testAddSingletonWithScopes3() {
        String expected = "MyValue";

        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope2.addSingleton(String.class, expected);

        // assert

        Optional<String> actualParent = parent.toProvider().get(String.class);
        Optional<String> actualScope1 = scope1.toProvider().get(String.class);

        assertTrue(actualParent.isPresent());
        assertTrue(actualScope1.isPresent());

        assertSame(expected, actualParent.get());
        assertSame(expected, actualScope1.get());
    }

    @Test
    public void testAddTransientWithScopes1() {
        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope1.addTransient(IService.class, Service.class);

        // assert

        Optional<IService> actualParent = parent.toProvider().get(IService.class);
        Optional<IService> actualScope1 = scope1.toProvider().get(IService.class);
        Optional<IService> actualScope2 = scope2.toProvider().get(IService.class);

        assertTrue(actualParent.isPresent());
        assertTrue(actualScope1.isPresent());
        assertTrue(actualScope2.isPresent());

        assertNotSame(actualParent.get(), actualScope1.get());
        assertNotSame(actualParent.get(), actualScope2.get());
        assertNotSame(actualScope1.get(), actualScope2.get());
    }

    @Test
    public void testAddTransientWithScopes2() {
        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope2.addTransient(IService.class, Service.class);

        // assert

        Optional<IService> actualParent = parent.toProvider().get(IService.class);
        Optional<IService> actualScope1 = scope1.toProvider().get(IService.class);
        Optional<IService> actualScope2 = scope2.toProvider().get(IService.class);

        assertTrue(actualParent.isPresent());
        assertTrue(actualScope1.isPresent());
        assertTrue(actualScope2.isPresent());

        assertNotSame(actualParent.get(), actualScope1.get());
        assertNotSame(actualParent.get(), actualScope2.get());
        assertNotSame(actualScope1.get(), actualScope2.get());
    }

    @Test
    public void testAddScopedWithScopes1() {
        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope1.addScoped(IService.class, Service.class);

        // assert

        Optional<IService> actualParent = parent.toProvider().get(IService.class);
        Optional<IService> actualScope1 = scope1.toProvider().get(IService.class);
        Optional<IService> actualScope2 = scope2.toProvider().get(IService.class);

        assertTrue(actualParent.isEmpty());
        assertTrue(actualScope1.isPresent());
        assertTrue(actualScope2.isEmpty());
    }

    @Test
    public void testAddScopedWithScopes2() {
        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope2.addScoped(Service.class);

        // assert

        Optional<IService> actualParent = parent.toProvider().get(IService.class);
        Optional<IService> actualScope1 = scope1.toProvider().get(IService.class);
        Optional<IService> actualScope2 = scope2.toProvider().get(IService.class);

        assertTrue(actualParent.isEmpty());
        assertTrue(actualScope1.isEmpty());
        assertTrue(actualScope2.isPresent());
    }

    @Test
    public void testAddScopedWithScopes3() {
        String expected = "MyValue";

        // arrange
        var parent = new WirerServiceCollection();
        var scope1 = new WirerScopedServiceCollection(parent);
        var scope2 = new WirerScopedServiceCollection(parent);

        // act
        scope1.addScoped(String.class, expected);

        // assert
        Optional<String> actualParent = parent.toProvider().get(String.class);
        Optional<String> actualScope1 = scope1.toProvider().get(String.class);
        Optional<String> actualScope2 = scope2.toProvider().get(String.class);

        assertTrue(actualParent.isEmpty());
        assertTrue(actualScope1.isPresent());
        assertTrue(actualScope2.isEmpty());

        assertEquals(expected, actualScope1.get());
    }

    static class Service implements IService { }

    interface IService { }
}
