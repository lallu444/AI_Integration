package org.example.hooks;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.example.drivers.DriverManager;
import org.example.utilities.Property;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public class Hooks {
    @Before
    public void setUp() throws MalformedURLException {
        // Initialize WebDriver
        DriverManager.initializeDriver(Property.browser);
        //WebDriver driver = DriverManager.getDriver();
        //driver.manage().window().maximize();

        System.out.println("Browser initialized and window maximized");
    }
    @After
    public void tearDown() {
        // Close the WebDriver
        DriverManager.quitDriver();
        System.out.println("Browser closed");
    }
}
