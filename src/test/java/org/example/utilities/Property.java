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
        } catch (IOException e) {
            System.out.println("Warning: ConfigProperties.properties file not found. Relying strictly on command-line arguments.");
        }

        // Check command-line system property (-D) first. If null, fall back to file property.
        platform = System.getProperty("Platform", props.getProperty("Platform"));
        browser = System.getProperty("Browser", props.getProperty("Browser"));
        Role = System.getProperty("Role", props.getProperty("Role"));
        Experince = System.getProperty("Experince", props.getProperty("Experince"));
        Mail = System.getProperty("Mail", props.getProperty("Mail"));
        Location = System.getProperty("Location", props.getProperty("Location"));
    }
}
