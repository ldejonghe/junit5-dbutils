package org.ldejonghe.utils.junit5.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method that is expected to fail with a specific exception type
 * thrown during the test execution or the extensions phase.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExpectFailure {
    /**
     * The exception type that is expected.
     * Defaults to AssertionError.
     */
    Class<? extends Throwable> value() default AssertionError.class;
}
