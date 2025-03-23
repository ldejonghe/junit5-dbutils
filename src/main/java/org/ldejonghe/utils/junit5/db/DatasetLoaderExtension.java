package org.ldejonghe.utils.junit5.db;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


/**
 * JUnit 5 Extension that automatically loads a dataset before each test method execution.
 * <p>
 * The extension checks if the test method is annotated with {@link LoadDataset}.
 * If present, it loads the specified dataset XML file into the database using the provided {@link DataSource}.
 * </p>
 * 
 * <p>Usage example in a test class:</p>
 * <pre>
 * {@code
 * @ExtendWith(DatasetLoaderExtension.class)
 * class MyDatabaseTest {
 * 
 *     @LoadDataset("datasets/test-data.xml")
 *     @Test
 *     void testDatabaseOperation() {
 *         // test logic here
 *     }
 * }
 * }
 * </pre>
 */
public class DatasetLoaderExtension implements BeforeEachCallback {

    private final DataSource dataSource;

    /**
     * Creates a new instance of {@code DatasetLoaderExtension} with the specified {@link DataSource}.
     *
     * @param dataSource the data source used to obtain database connections
     */
    public DatasetLoaderExtension(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Callback method that is invoked before each test method execution.
     * If the test method is annotated with {@link LoadDataset}, the specified dataset XML file
     * is loaded into the database.
     *
     * @param context the current extension context; never {@code null}
     * @throws Exception if any database or dataset loading error occurs
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        LoadDataset annotation = testMethod.getAnnotation(LoadDataset.class);
        if (annotation != null) {
            String datasetFile = annotation.value();
            try (Connection connection = dataSource.getConnection()) {
                new XmlDataLoader().load(datasetFile, connection);
            }
        }
    }
}

