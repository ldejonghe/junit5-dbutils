package org.ldejonghe.utils.junit5.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@ExtendWith(LoadDatasetExtension.class)
public class XmlDataLoaderTest {

	private static final Logger logger = LoggerFactory.getLogger(XmlDataLoaderTest.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private XmlDataLoader xmlDataLoader;

	@Test
	void testXmlDataLoaderInsertsUsers() throws Exception {
		System.out.println("in test");
		try (Connection conn = dataSource.getConnection()) {
			xmlDataLoader.load("src/test/resources/test-users.xml", conn);
		}
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);
		logger.info("Number of users inserted: {}", count);

		assertThat(count).isEqualTo(2);
	}

	@Test
	@LoadDataset("src/test/resources/test-users.xml")
	void testDatasetLoad() {
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);
		assertThat(count).isEqualTo(2);

		Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_roles", Integer.class);
		assertThat(roleCount).isEqualTo(2);
	}

}
