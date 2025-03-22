package org.ldejonghe.utils.junit5.db;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.sql.*;

public class XmlDataLoader {
    public void load(String xmlFilePath, Connection connection) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFilePath);
        NodeList users = doc.getElementsByTagName("user");
        for (int i = 0; i < users.getLength(); i++) {
            Element user = (Element) users.item(i);
            String id = user.getAttribute("id");
            String name = user.getAttribute("name");
            String age = user.getAttribute("age");
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO user (id, name, age) VALUES (?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, name);
                ps.setInt(3, Integer.parseInt(age));
                ps.executeUpdate();
            }
        }
    }
}
