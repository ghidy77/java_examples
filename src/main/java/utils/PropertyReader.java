package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private static final Logger LOG = LogManager.getLogger(PropertyReader.class);
    public static final String CONFIG_FILE = "src/test/resources/config.properties";

    private PropertyReader() {
        // should not be instantiated
    }

    public static String getProperty(String propertyName){
        String systemProperty = System.getProperty(propertyName);
        // System Property has priority
        if (systemProperty != null){
            return systemProperty;
        } else {
            // Fallback on File Property
            Properties fileProperties = new Properties();
            try (InputStream is = new FileInputStream(CONFIG_FILE)){
                fileProperties.load(is);
            }catch (IOException e){
                LOG.warn("Config File not found", e);
            };
            return fileProperties.getProperty(propertyName);
        }
    }
}
