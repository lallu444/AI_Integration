# Selenium Java Cucumber BDD TestNG Framework
## Project Overview
This is a comprehensive Selenium Java Cucumber BDD (Behavior-Driven Development) TestNG framework designed for automated testing of web applications. It includes a demo automation for Naukri job search without login.
## Framework Structure
### Folder Structure
\\\
AI_Integration/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── drivers/           # WebDriver management
│   │   │   ├── pages/             # Page Object Model (POM)
│   │   │   └── utilities/         # Utility classes
│   │   └── resources/             # Configuration files (log4j2.xml)
│   └── test/
│       ├── java/org/example/
│       │   ├── stepDefinitions/   # Cucumber step definitions
│       │   ├── runners/           # Cucumber test runners
│       │   └── hooks/             # Cucumber hooks (setup/teardown)
│       └── resources/
│           ├── features/          # Cucumber feature files
│           └── testng.xml         # TestNG configuration
├── pom.xml                        # Maven configuration
└── README.md
\\\
## Dependencies
### Key Dependencies
- **Selenium WebDriver 4.15.0** - Web automation framework
- **Cucumber 7.14.0** - BDD framework for test automation
- **TestNG 7.8.1** - Testing framework
- **WebDriverManager 5.6.2** - Automatic WebDriver management
- **Log4j 2.20.0** - Logging framework
- **Gson 2.10.1** - JSON handling
- **Allure 2.21.0** - Reporting framework
## Key Components
### 1. DriverManager (drivers/DriverManager.java)
Manages WebDriver initialization and lifecycle
- Supports multiple browsers (Chrome, Firefox, Edge)
- Thread-safe implementation using ThreadLocal
- Automatic driver setup using WebDriverManager
### 2. BasePage (pages/BasePage.java)
Base class for Page Object Model
- Common web element interactions
- Explicit waits for element visibility and clickability
- Reusable methods for sending keys, clicking, etc.
### 3. NaukriJobSearchPage (pages/NaukriJobSearchPage.java)
Page class for Naukri job search functionality
- Locators for job search elements
- Methods for skill search, experience selection, location entry
- Retrieves top 10 job postings with details and apply links
### 4. Hooks (hooks/Hooks.java)
Cucumber hooks for test lifecycle
- \@Before\ - Initialize browser
- \@After\ - Close browser and cleanup
### 5. Step Definitions (stepDefinitions/NaukriJobSearchSteps.java)
Maps Cucumber scenarios to Java code
- Given/When/Then steps for job search workflow
- Prints job details to console
### 6. Feature Files (features/NaukriJobSearch.feature)
BDD scenarios in Gherkin language
- Human-readable test scenarios
### 7. Test Runner (runners/CucumberRunner.java)
Executes Cucumber scenarios via TestNG
- Configured with glue paths and plugins
- Generates HTML and JSON reports
## Setup Instructions
### Prerequisites
- Java 17+
- Maven 3.6+
- Chrome/Firefox browser
### Installation Steps
1. **Clone or navigate to project**
   \\\ash
   cd C:\Users\laxminarasimha.b\AI_Integration
   \\\
2. **Download dependencies**
   \\\ash
   mvn clean install
   \\\
3. **Build the project**
   \\\ash
   mvn clean compile
   \\\
## Running Tests
### Using Maven
\\\ash
# Run all tests
mvn test
# Run specific test runner
mvn test -Dtest=CucumberRunner
# Run with Cucumber options
mvn test -Drunner=CucumberRunner
\\\
### Using TestNG
\\\ash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
\\\
## Test Execution Flow
1. **Hooks.setUp()** - Initializes WebDriver
2. **Feature File Steps** - Executes Gherkin scenarios
   - Navigates to Naukri
   - Enters QA Engineer as skill
   - Selects 5 Years experience
   - Enters Hyderabad as location
   - Clicks search
3. **Job Retrieval** - Fetches top 10 job postings
4. **Console Output** - Prints job details with apply links
5. **Hooks.tearDown()** - Closes WebDriver
## Sample Output
The framework prints job details in the following format:
\\\
========== TOP 10 JOB POSTINGS ==========
Job 1:
  Title: QA Engineer
  Company: TechCorp Solutions
  Posted: 2 hours ago
  Apply Link: https://www.naukri.com/apply/...
[More jobs...]
========== END OF JOB LIST ==========
Total jobs retrieved: 10
\\\
## Reports
### Generated Reports
- **HTML Report**: target/cucumber-reports/cucumber.html
- **JSON Report**: target/cucumber-reports/cucumber.json
- **Logs**: logs/automation.log
### View Reports
\\\ash
# Open HTML report
target\cucumber-reports\cucumber.html
\\\
## Configuration Files
### pom.xml
Main Maven configuration with all dependencies
### testng.xml
TestNG suite configuration
### log4j2.xml
Logging configuration for console and file output
## Extensibility
### Adding New Test Cases
1. Create new feature file in \src/test/resources/features/\
2. Create corresponding page class in \src/main/java/org/example/pages/\
3. Add step definitions in \src/test/java/org/example/stepDefinitions/\
### Adding New Pages
1. Create new class extending \BasePage\
2. Define locators as private By variables
3. Implement page-specific methods
### Changing Browser
Edit \Hooks.java\ setUp() method:
\\\java
DriverManager.initializeDriver("firefox"); // or "edge"
\\\
## Troubleshooting
### Issue: Tests not finding elements
- Increase wait time in BasePage.java
- Verify locators match current website structure
- Check browser driver compatibility
### Issue: Driver not initializing
- Ensure WebDriver Manager has internet connection
- Check Java and Maven are properly installed
- Verify pom.xml dependencies
### Issue: Naukri website structure changed
- Update locators in NaukriJobSearchPage.java
- Inspect website elements using browser dev tools
## Best Practices
1. **Use Page Object Model** - Separate page logic from tests
2. **Wait Strategies** - Use explicit waits instead of Thread.sleep()
3. **Error Handling** - Implement try-catch for robustness
4. **Data-Driven Tests** - Use Cucumber data tables for multiple scenarios
5. **Logging** - Log all important actions for debugging
## Notes
- This framework is designed as a demo for Naukri job search without login
- Real-world applications may require login handling
- Locators may need updates if website structure changes
- Consider implementing headless browser mode for CI/CD pipelines
## License
Open source - Feel free to modify and use as needed
