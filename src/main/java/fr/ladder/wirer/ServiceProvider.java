package fr.ladder.wirer;

import java.util.Optional;

/**
 * @author Snowtyy
 */
public interface ServiceProvider {

    /**
     * Returns the service instance associated with the given class.
     *
     * @param clazz the class of the service to retrieve
     * @return an Optional containing the service instance if found, otherwise empty
     * @param <T> the type of the service
     */
    <T> Optional<T> get(Class<T> clazz);
}
