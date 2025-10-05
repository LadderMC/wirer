package fr.ladder.di;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * @author Snowtyy
 */
public interface PluginInspector {

    Stream<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotation);

    Stream<Field> getFieldsWithAnnotation(Class<? extends Annotation> annotation);
}
