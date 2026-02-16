package fr.ladder.wirer;

import java.util.Optional;

/**
 * @author Snowtyy
 */
public interface ServiceProvider {

    <T> Optional<T> get(Class<T> clazz);

}
