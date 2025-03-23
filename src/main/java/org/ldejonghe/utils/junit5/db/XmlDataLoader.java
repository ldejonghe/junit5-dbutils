package org.ldejonghe.utils.junit5.db;

import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlDataLoader {

    public void load(String xmlFilePath, Connection connection) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                                             .newDocumentBuilder()
                                             .parse(xmlFilePath);

        Element root = doc.getDocumentElement();
        NodeList tableNodes = root.getChildNodes();

        for (int i = 0; i < tableNodes.getLength(); i++) {
            Node node = tableNodes.item(i);

            // Only process ELEMENT nodes (skip text, comments)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String tableName = element.getTagName(); // Tag name is the table name

                NamedNodeMap attributes = element.getAttributes();
                List<String> columns = new ArrayList<>();
                List<String> placeholders = new ArrayList<>();
                List<String> values = new ArrayList<>();

                for (int j = 0; j < attributes.getLength(); j++) {
                    Attr attr = (Attr) attributes.item(j);
                    columns.add(attr.getName());
                    placeholders.add("?");
                    values.add(attr.getValue());
                }

                String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                        tableName,
                        String.join(", ", columns),
                        String.join(", ", placeholders)
                );

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    for (int k = 0; k < values.size(); k++) {
                        ps.setString(k + 1, values.get(k));  // You could improve typing later
                    }
                    ps.executeUpdate();
                }
            }
        }
    }
}
