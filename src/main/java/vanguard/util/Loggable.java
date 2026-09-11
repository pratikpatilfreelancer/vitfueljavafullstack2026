package vanguard.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom, user-defined annotation. @Retention and @Target below are
 * themselves "meta-annotations" — annotations that describe how this
 * annotation may be used and how long it survives (here, kept
 * available at runtime so AnnotationScanner can read it via
 * reflection — see that class for the payoff).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {
    String value() default "";
}
