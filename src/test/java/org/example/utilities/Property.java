package org.example.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {
    public static String platform;
    public static String browser;
    public static String Role;
    public static String Experince;
    public static String Mail;
    public static String Location;

    static {
        Properties props = new Properties();
        String filePath = "src/test/resources/ConfigProperties.properties";

        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
            platform = props.getProperty("Platform");
            browser = props.getProperty("Browser");
            Role = props.getProperty("Role");
            Experince = props.getProperty("Experince");
            Mail = props.getProperty("Mail");
            Location = props.getProperty("Location");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file.", e);
        }
    }
}
