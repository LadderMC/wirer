package fr.ladder.wirer.base;

import fr.ladder.wirer.exception.NotInstantiableException;

import java.lang.reflect.Modifier;

/**
 * @author Snowtyy
 **/
public class WirerUtils {
    
    private WirerUtils() {
        throw new UnsupportedOperationException("This is static class and cannot be instantiated");
    }
    
    /**
     * Determines if a class can be directly instantiated.
     * <p>
     * Returns {@code true} if the class is a concrete, non-primitive type.
     * Specifically, it excludes:
     * <ul>
     * <li>Interfaces and Abstract classes</li>
     * <li>Primitive types (e.g., {@code int.class})</li>
     * <li>Array types</li>
     * </ul>
     *
     * @param clazz the class to check.
     * @return {@code true} if the class is instantiable, otherwise {@code false}.
     */
    public static boolean isInstantiable(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        
        return !clazz.isInterface()
                && !Modifier.isAbstract(modifiers)
                && !clazz.isPrimitive()
                && !clazz.isArray();
    }
    
    /**
     * Ensures that the provided class can be instantiated.
     * <p>
     * This method validates that the class is a concrete type. It throws an
     * {@link NotInstantiableException} if the class is an interface,
     * abstract, primitive, or an array.
     * </p>
     *
     * @param clazz the class to validate.
     * @throws NotInstantiableException if the class is not instantiable.
     */
    public static void ensureInstantiable(Class<?> clazz) {
        if (!isInstantiable(clazz)) {
            throw new NotInstantiableException(clazz);
        }
    }
}
