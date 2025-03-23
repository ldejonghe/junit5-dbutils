package org.ldejonghe.utils.junit5.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@ExtendWith({LoadDatasetExtension.class, ExpectedDataSetExtension.class})
public class XmlDataLoaderTest {

	private static final Logger logger = LoggerFactory.getLogger(XmlDataLoaderTest.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private XmlDataLoader xmlDataLoader;

	@Autowired
	Flyway flyway;
	
	@Test
	void testXmlDataLoaderInsertsUsers() throws Exception {
		logger.debug("in test");
		flyway.clean();
		flyway.migrate();
		try (Connection conn = dataSource.getConnection()) {
			xmlDataLoader.load("src/test/resources/test-users.xml", conn);
		}
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);
		logger.info("Number of users inserted: {}", count);

		assertThat(count).isEqualTo(2);
	}

	@Test
	@LoadDataset(value = "src/test/resources/test-users.xml", clean = true)
	void testDatasetLoad() {
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);
		assertThat(count).isEqualTo(2);

		Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_roles", Integer.class);
		assertThat(roleCount).isEqualTo(2);
	}

	@Test
	@ExpectedDataSet("src/test/resources/expected-users.xml")
	@LoadDataset(value = "src/test/resources/test-users.xml", clean = true)
	void testExpectedDataSetValidation() {
		// This test should not fail, because the expected dataset is the same as the one loaded
	}

	
	@Test
	@LoadDataset("src/test/resources/test-users.xml")
	@ExpectedDataSet("src/test/resources/expected-users_NOK.xml")
	//@ExpectFailure(ExpectedDataSetMismatchException.class) //Does not work yet due to junit 5 lifecycle.
	@Disabled
	void testExpectedDataSetValidation_mismatch_shoyldFail() {
		
	}
	
	
	@Test
	@LoadDataset("src/test/resources/test-users.xml")
	@ExpectedDataSet("src/test/resources/expected-users.xml")
	//@ExpectFailure(ExpectedDataSetMismatchException.class) //Does not work yet due to junit 5 lifecycle.
	@Disabled
	void testExpectedDataSetValidation_shouldFail() {
	    // Change the age of user with id=1 to 80
	    jdbcTemplate.update("UPDATE app_user SET age = ? WHERE id = ?", 80, 1);

	    // At this point, the database no longer matches the expected dataset
	    // The @ExpectedDataSet validation will fail after the test method finishes
	}
	
	
}
