package org.ldejonghe.utils.junit5.db;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * JUnit 5 extension that validates the database content against
 * an expected dataset defined in an XML file using DBUnit-style format.
 *
 * <p>The extension reads the @ExpectedDataSet annotation on the test method,
 * loads the XML, and for each row defined in the dataset, verifies that
 * a matching record exists in the database.</p>
 *
 * <p>Example expected dataset XML:</p>
 * <pre>{@code
 * <dataset>
 *     <app_user id="1" name="Alice" age="30"/>
 *     <app_user id="2" name="Bob" age="25"/>
 * </dataset>
 * }</pre>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * @ExpectedDataSet("src/test/resources/expected-users.xml")
 * @Test
 * void myTest() {
 *     // Your test code
 * }
 * }</pre>
 *
 * <p>If any expected row is missing, the test will fail with an AssertionError.</p>
 */
public class ExpectedDataSetExtension implements AfterEachCallback {

    /**
     * Called by JUnit after each test execution.
     * If @ExpectedDataSet is present, it performs dataset validation.
     *
     * @param context JUnit ExtensionContext providing test method details and application context
     * @throws Exception if dataset file reading or database query fails
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var method = context.getRequiredTestMethod();
        var expectedDataSet = method.getAnnotation(ExpectedDataSet.class);
        if (expectedDataSet == null) return; // Skip if no annotation

        // Retrieve Spring's application context and JDBC template
        var appContext = SpringExtension.getApplicationContext(context);
        JdbcTemplate jdbcTemplate = appContext.getBean(JdbcTemplate.class);

        // Parse the expected dataset XML
        Document doc = DocumentBuilderFactory.newInstance()
                                             .newDocumentBuilder()
                                             .parse(expectedDataSet.value());
        NodeList expectedTables = doc.getDocumentElement().getChildNodes();

        // Process each expected row in the dataset
        for (int i = 0; i < expectedTables.getLength(); i++) {
            Node node = expectedTables.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;

            Element expectedRow = (Element) node;
            String tableName = expectedRow.getTagName();

            NamedNodeMap attributes = expectedRow.getAttributes();
            StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
            List<Object> params = new ArrayList<>();

            // Build WHERE clause based on attributes
            for (int j = 0; j < attributes.getLength(); j++) {
                String column = attributes.item(j).getNodeName();
                String value = attributes.item(j).getNodeValue();
                whereClause.append(" AND ").append(column).append(" = ?");
                params.add(value);
            }

            // Query database to verify the expected row exists
            String sql = "SELECT COUNT(*) FROM " + tableName + whereClause;
            Integer count = jdbcTemplate.queryForObject(sql, params.toArray(), Integer.class);

            if (count == null || count == 0) {
            	String message = "Expected row not found in table '" + tableName + "' with attributes: " + getAttributesString(attributes);
            	throw new ExpectedDataSetMismatchException(message);
            	//below was not working because The ExpectedDataSetExtension triggers the AssertionError in its afterEach() phase
            	//@ExpectFailure(AssertionError.class) is handled by ExpectFailureExtensionExpectFailureExtension must wrap both:
            	//The test method execution    
            	//All extensions' execution including afterEach
                //throw new AssertionError("Expected row not found in table '" + tableName + "' with attributes: " + getAttributesString(attributes));
            }
        }
    }

    /**
     * Helper method to convert XML attributes to a readable string format for error messages.
     *
     * @param attributes NamedNodeMap of attributes from the XML element
     * @return formatted string of attributes
     */
    private String getAttributesString(NamedNodeMap attributes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attributes.getLength(); i++) {
            sb.append(attributes.item(i).getNodeName())
              .append("=")
              .append("\"")
              .append(attributes.item(i).getNodeValue())
              .append("\" ");
        }
        return sb.toString().trim();
    }
}
