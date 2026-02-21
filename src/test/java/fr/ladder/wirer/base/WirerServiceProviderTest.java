package fr.ladder.wirer.base;

import fr.ladder.wirer.base.mock.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Snowtyy
 */
public class WirerServiceProviderTest {

    private WirerServiceCollection collection;

    @BeforeEach
    public void setup() {
        collection = new WirerServiceCollection();
    }

    @Test
    public void testGet1() {
        // arrange
        collection.addSingleton(IService.class, Service.class);
        collection.addTransient(ITransient.class, Transient.class);
        var provider = collection.toProvider();

        // act
        Optional<IService> opt1 = provider.get(IService.class);
        Optional<IService> opt2 = provider.get(IService.class);
        Optional<ITransient> opt3 = provider.get(ITransient.class);
        Optional<ITransient> opt4 = provider.get(ITransient.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());
        assertTrue(opt3.isPresent());
        assertTrue(opt4.isPresent());

        assertSame(opt1.get(), opt2.get());
        assertNotSame(opt3.get(), opt4.get());
    }

    @Test
    public void testGet2() {
        // arrange
        collection.addTransient(ITransient.class, Transient.class);
        var provider = collection.toProvider();

        // act
        Optional<Transient> opt1 = provider.get(Transient.class);
        Optional<Transient> opt2 = provider.get(Transient.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());

        assertNotSame(opt1.get(), opt2.get());
    }

    @Test
    public void testGet3() {
        // arrange
        collection.addSingleton(IService.class, Service.class);
        var provider = collection.toProvider();

        // act
        Optional<Service> opt1 = provider.get(Service.class);
        Optional<Service> opt2 = provider.get(Service.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());

        assertSame(opt1.get(), opt2.get());
    }

    @Test
    public void testResolve1() {
        Service expectedService = new Service();

        // arrange
        collection.addSingleton(IService.class, expectedService);
        collection.addTransient(ITransient.class, Transient.class);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.resolve(IService.class);
        Optional<Object> opt2 = provider.resolve(ITransient.class);
        Optional<Object> opt3 = provider.resolve(ITransient.class);

        // assert
        assertTrue(opt1.isPresent());
        assertEquals(expectedService, opt1.get());

        assertTrue(opt2.isPresent());
        assertTrue(opt3.isPresent());
        assertNotSame(opt2.get(), opt3.get());
    }

    @Test
    public void testResolve2() {
        Service expectedService = new Service();

        // arrange
        collection.addSingleton(IService.class, Service.class);
        collection.addSingleton(Service.class, expectedService);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.resolve(Service.class);
        Optional<Object> opt2 = provider.resolve(IService.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());
        assertEquals(expectedService, opt1.get());
        assertEquals(expectedService, opt2.get());
    }

    @Test
    public void testResolve3() {
        // arrange
        collection.addSingleton(IService.class, Service.class);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.resolve(IService.class);
        Optional<Object> opt2 = provider.resolve(Service.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isEmpty());
    }

    @Test
    public void testFinallyResolveTest1() {
        Service expectedService = new Service();

        // arrange
        collection.addSingleton(IService.class, expectedService);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.finallyResolve(Service.class);

        // assert
        assertTrue(opt1.isPresent());
        assertEquals(expectedService, opt1.get());
    }

    @Test
    public void testFinallyResolveTest2() {
        // arrange
        collection.addSingleton(IService.class, Service.class);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.finallyResolve(Service.class);
        Optional<IService> opt2 = provider.get(IService.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());
        assertSame(opt1.get(), opt2.get());
    }

    @Test
    public void testFinallyResolveTest3() {
        // arrange
        collection.addTransient(ITransient.class, Transient.class);
        var provider = collection.toProvider();

        // act
        Optional<Object> opt1 = provider.finallyResolve(Transient.class);
        Optional<Object> opt2 = provider.finallyResolve(Transient.class);

        // assert
        assertTrue(opt1.isPresent());
        assertTrue(opt2.isPresent());
        assertNotSame(opt1.get(), opt2.get());
    }

}
