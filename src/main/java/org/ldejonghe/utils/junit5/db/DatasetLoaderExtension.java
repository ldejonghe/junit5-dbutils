package org.ldejonghe.utils.junit5.db;

import org.junit.jupiter.api.extension.*;
import java.lang.reflect.Method;
import javax.sql.DataSource;
import java.sql.Connection;

public class DatasetLoaderExtension implements BeforeEachCallback {

    private final DataSource dataSource;

    public DatasetLoaderExtension(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
