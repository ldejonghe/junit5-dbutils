package org.ldejonghe.utils.junit5.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify a dataset XML file that should be loaded 
 * into the database before the execution of a test method.
 * <p>
 * This annotation is processed by {@link DatasetLoaderExtension}, which 
 * loads the specified dataset into the database to prepare the test environment.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @LoadDataset("datasets/test-data.xml")
 * @Test
 * void testDatabaseOperation() {
 *     // test logic that depends on the loaded dataset
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoadDataset {

    /**
     * The path to the dataset XML file to be loaded.
     *
     * @return the dataset file path relative to the classpath or file system
     */
    String value();
    boolean clean() default true;  // Perform Flyway clean/migrate by default
}
