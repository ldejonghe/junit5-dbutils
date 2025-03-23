package org.ldejonghe.utils.junit5.db;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.sql.*;

import org.springframework.stereotype.Component;
/**
 * Utility class responsible for loading user data from an XML dataset file
 * and inserting it into a database table named {@code user}.
 * <p>
 * The expected XML format is as follows:
 * </p>
 * <pre>
 * {@code
 * <dataset>
 *     <user id="1" name="John Doe" age="30"/>
 *     <user id="2" name="Jane Smith" age="25"/>
 * </dataset>
 * }
 * </pre>
 * <p>
 * Each {@code user} element is parsed, and the attributes {@code id}, {@code name},
 * and {@code age} are extracted and inserted into the {@code user} table.
 * </p>
 */
@Component
public class XmlDataLoader {

    /**
     * Loads user data from the specified XML file and inserts the data into the {@code user} table.
     *
     * @param xmlFilePath the path to the XML dataset file
     * @param connection the active JDBC connection used to perform the insert operations
     * @throws Exception if an error occurs during XML parsing or database interaction
     */
    public void load(String xmlFilePath, Connection connection) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                                             .newDocumentBuilder()
                                             .parse(xmlFilePath);
        NodeList users = doc.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            String id = user.getAttribute("id");
            String name = user.getAttribute("name");
            String age = user.getAttribute("age");
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO app_user (id, name, age) VALUES (?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, name);
                ps.setInt(3, Integer.parseInt(age));
                ps.executeUpdate();
            }
        }
    }
}
