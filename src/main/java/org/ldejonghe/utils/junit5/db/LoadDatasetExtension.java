package org.ldejonghe.utils.junit5.db;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;

public class LoadDatasetExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        LoadDataset dataset = testMethod.getAnnotation(LoadDataset.class);
        if (dataset != null) {
            // Access Spring Context
            DataSource ds = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
            XmlDataLoader loader = SpringExtension.getApplicationContext(context).getBean(XmlDataLoader.class);
            try (Connection conn = ds.getConnection()) {
                loader.load(dataset.value(), conn);
            }
        }
    }
}
