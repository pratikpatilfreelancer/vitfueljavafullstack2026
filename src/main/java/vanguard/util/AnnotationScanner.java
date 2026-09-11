package vanguard.util;

import java.lang.reflect.Method;

/**
 * Scans a class's methods at runtime for the @Loggable annotation and
 * prints what it finds. This is what makes the custom annotation
 * actually *do* something, via java.lang.reflect, rather than being
 * purely decorative.
 */
public final class AnnotationScanner {

    private AnnotationScanner() { }

    public static void scan(Class<?> target) {
        System.out.println("--- Annotation scan: " + target.getSimpleName() + " ---");
        for (Method method : target.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Loggable.class)) {
                Loggable tag = method.getAnnotation(Loggable.class);
                System.out.printf("  @Loggable(\"%s\") -> %s()%n", tag.value(), method.getName());
            }
        }
    }
}
