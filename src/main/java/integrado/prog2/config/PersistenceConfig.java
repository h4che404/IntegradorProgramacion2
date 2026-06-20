package integrado.prog2.config;

import integrado.prog2.exception.DataAccessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PersistenceConfig {
    private final Map<String, String> properties;

    private PersistenceConfig(Map<String, String> properties) {
        this.properties = properties;
    }

    public static PersistenceConfig load() {
        try (InputStream inputStream = PersistenceConfig.class.getClassLoader().getResourceAsStream("persistence.xml")) {
            if (inputStream == null) {
                throw new DataAccessException("persistence.xml was not found in resources.");
            }

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            NodeList propertyNodes = document.getElementsByTagName("property");
            Map<String, String> properties = new HashMap<>();

            for (int i = 0; i < propertyNodes.getLength(); i++) {
                Element property = (Element) propertyNodes.item(i);
                properties.put(property.getAttribute("name"), property.getAttribute("value"));
            }

            return new PersistenceConfig(properties);
        } catch (Exception exception) {
            throw new DataAccessException("Could not load database configuration.", exception);
        }
    }

    public String getRequiredProperty(String key) {
        String value = properties.get(key);
        if (value == null || value.isBlank()) {
            throw new DataAccessException("Missing required database property: " + key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = properties.get(key);
        return (value == null) ? defaultValue : value;
    }
}
