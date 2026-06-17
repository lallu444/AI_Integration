package org.example.drivers;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.utilities.Property;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    public static final String USERNAME = "lallubanala444";
    public static final String ACCESS_KEY = "LT_pxmul8RDoUpbZttNPHw2Xfc4hCG6mZEztlnpO4P81nqLcIz";
    public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub.lambdatest.com/wd/hub";

    public static void initializeDriver(String browserType) throws MalformedURLException {
        System.out.println("this is to inticalize driver: "+Property.platform +Property.browser);
        if(Property.platform.equalsIgnoreCase("LambdaTest")){
            switch (browserType.toLowerCase()) {
                case "chrome":
                    ChromeOptions browserOptions = new ChromeOptions();
                    browserOptions.setPlatformName("Windows 10");
                    browserOptions.setBrowserVersion("dev");
                    HashMap<String, Object> ltOptions = new HashMap<String, Object>();
//                    ltOptions.put("username", "lallubanala444");
//                    ltOptions.put("accessKey", "LT_pxmul8RDoUpbZttNPHw2Xfc4hCG6mZEztlnpO4P81nqLcIz");
                    ltOptions.put("project", "AI Automation Sample Project");
                    ltOptions.put("build", "AI Automation Sample Build");
                    ltOptions.put("name", "AI Automation Sample Test");
                    ltOptions.put("selenium_version", "4.0.0");
                    ltOptions.put("w3c", true);
                    browserOptions.setCapability("LT:Options", ltOptions);
                    driver.set(new RemoteWebDriver(new URL(URL),browserOptions));
                    break;
                /*case "firefox":
                    driver.set(LambdaTestDriver.getFirefoxDriver());
                    break;
                case "edge":
                    driver.set(LambdaTestDriver.getEdgeDriver());
                    break;*/
                default:
                    driver.set(new ChromeDriver());
            }

        }else{
            switch (browserType.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    driver.set(new ChromeDriver());
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver.set(new FirefoxDriver());
                    break;
                case "edge":
                    WebDriverManager.edgedriver().setup();
                    driver.set(new EdgeDriver());
                    break;
                default:
                    WebDriverManager.chromedriver().setup();
                    driver.set(new ChromeDriver());
            }
        }
    }
    public static WebDriver getDriver() {
        return driver.get();
    }
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}