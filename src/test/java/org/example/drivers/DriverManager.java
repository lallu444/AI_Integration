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
import java.util.Collections;
import java.util.HashMap;

public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Reads environment variables from GitHub Actions, defaults to hardcoded strings if local
    public static final String USERNAME = System.getenv("LT_USERNAME") != null ? System.getenv("LT_USERNAME") : "lallubanala444";
    public static final String ACCESS_KEY = System.getenv("LT_ACCESS_KEY") != null ? System.getenv("LT_ACCESS_KEY") : "LT_pxmul8RDoUpbZttNPHw2Xfc4hCG6mZEztlnpO4P81nqLcIz";
    public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub.lambdatest.com/wd/hub";

    public static void initializeDriver(String browserType) throws MalformedURLException {
        System.out.println("this is to inticalize driver: "+Property.platform +Property.browser);
        if(Property.platform.equalsIgnoreCase("LambdaTest")){
            switch (browserType.toLowerCase()) {
                case "chrome":
                    ChromeOptions browserOptions = new ChromeOptions();
                    browserOptions.setPlatformName("Windows 10");

                    // Avoid using "dev" version as it triggers instability and high bot flags.
                    // Use a stable, mainstream version like "latest".
                    browserOptions.setBrowserVersion("latest");

                    // --- LOCAL CHROME ANTI-BOT ARGUMENTS ---
                    // Removes the "Chrome is being controlled by automated test software" bar
                    browserOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                    browserOptions.setExperimentalOption("useAutomationExtension", false);

                    // Hides the navigator.webdriver property from JavaScript checks
                    browserOptions.addArguments("--disable-blink-features=AutomationControlled");

                    // Standard real-user window setups
                    browserOptions.addArguments("--start-maximized");
                    browserOptions.addArguments("--disable-infobars");

                    // --- LAMBDATEST SPECIFIC VISIBILITY OPTIONS ---
                    HashMap<String, Object> ltOptions = new HashMap<String, Object>();
                    ltOptions.put("project", "AI Automation Sample Project");
                    ltOptions.put("build", "AI Automation Sample Build");
                    ltOptions.put("name", "AI Automation Sample Test");
                    ltOptions.put("selenium_version", "4.23.0"); // Update to match your framework version
                    ltOptions.put("w3c", true);

                    // Hides LambdaTest's internal automation hooks from Cloudflare/Akamai scripts
                    ltOptions.put("headless", false);
                    ltOptions.put("video", true); // Keeping video on helps simulate an active display session
                    ltOptions.put("network", true);

                    // Disable LambdaTest's default automation banners injected into the browser window
                    ltOptions.put("visual", true);

                    browserOptions.setCapability("LT:Options", ltOptions);
                    driver.set(new RemoteWebDriver(new URL(URL), browserOptions));
                    break;
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
