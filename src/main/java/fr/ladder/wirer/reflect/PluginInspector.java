package fr.ladder.wirer.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * @author Snowtyy
 */
public interface PluginInspector extends AutoCloseable {

    Stream<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation);

    Stream<Field> getFieldsWithAnnotation(Class<? extends Annotation> annotation);

    @Override
    void close();
}
