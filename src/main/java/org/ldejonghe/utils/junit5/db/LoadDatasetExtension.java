package org.ldejonghe.utils.junit5.db;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class LoadDatasetExtension implements BeforeEachCallback {

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		LoadDataset annotation = context.getRequiredTestMethod().getAnnotation(LoadDataset.class);
	
		Method testMethod = context.getRequiredTestMethod();
		LoadDataset dataset = testMethod.getAnnotation(LoadDataset.class);
		if (dataset != null) {
			ApplicationContext springContext = SpringExtension.getApplicationContext(context);

			// Optional database cleanup based on annotation attribute
			if (annotation.clean()) {
				Flyway flyway = springContext.getBean(Flyway.class);
				flyway.clean();
				flyway.migrate();
			}
			// Access Spring Context
			DataSource ds = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
			XmlDataLoader loader = SpringExtension.getApplicationContext(context).getBean(XmlDataLoader.class);
			try (Connection conn = ds.getConnection()) {
				loader.load(dataset.value(), conn);
			}
		}
	}
}
