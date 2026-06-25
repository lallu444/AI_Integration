package org.example.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.*;

@CucumberOptions(
        features = "src/test/resources/features",
        tags = "@MailReportPropertyReaderAPI_BCC",
        glue = {"org.example.stepDefinitions", "org.example.hooks"},
        dryRun = false,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class CucumberRunnerBatch extends AbstractTestNGCucumberTests {
    // Test runner for Cucumber scenarios
}
