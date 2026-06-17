# ===== SELENIUM JAVA CUCUMBER BDD TESTNG FRAMEWORK SETUP GUIDE =====
## PROJECT STRUCTURE CREATED:
\\\
AI_Integration/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── drivers/
│   │   │   │   └── DriverManager.java              [WebDriver lifecycle management]
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java                   [Base class for POM]
│   │   │   │   └── NaukriJobSearchPage.java        [Naukri job search page object]
│   │   │   └── utilities/                          [For future utilities]
│   │   └── resources/
│   │       └── log4j2.xml                          [Logging configuration]
│   └── test/
│       ├── java/org/example/
│       │   ├── hooks/
│       │   │   └── Hooks.java                      [Cucumber setup/teardown]
│       │   ├── stepDefinitions/
│       │   │   └── NaukriJobSearchSteps.java       [Gherkin step implementations]
│       │   └── runners/
│       │       └── CucumberRunner.java             [Test execution runner]
│       └── resources/
│           ├── features/
│           │   └── NaukriJobSearch.feature         [Gherkin test scenarios]
│           └── testng.xml                          [TestNG configuration]
├── pom.xml                                         [Maven dependencies & build]
├── README.md                                       [Documentation]
└── .gitignore                                      [Git ignore rules]
\\\
## DEPENDENCIES INCLUDED:
✓ Selenium WebDriver 4.15.0
✓ Cucumber 7.14.0 (Java & TestNG)
✓ TestNG 7.8.1
✓ WebDriverManager 5.6.2
✓ Log4j 2.20.0
✓ SLF4J 2.0.9
✓ Gson 2.10.1
✓ Apache Commons Lang 3.13.0
✓ Allure Reporting 2.21.0
✓ RestAssured 5.3.2
✓ JUnit 4.13.2
## QUICK START:
1. Download/Install Requirements:
   - Java 17+
   - Maven 3.6+
   - Chrome/Firefox browser
2. Navigate to project:
   \\\ash
   cd C:\Users\laxminarasimha.b\AI_Integration
   \\\
3. Clean install dependencies:
   \\\ash
   mvn clean install
   \\\
4. Run tests:
   \\\ash
   mvn test
   \\\
## FRAMEWORK FEATURES:
### Page Object Model (POM)
- BasePage: Common methods for web interactions
- NaukriJobSearchPage: Specific page for job search automation
- Inheritance pattern for code reusability
### Behavior-Driven Development (BDD)
- Feature files in Gherkin language
- Given-When-Then step definitions
- Human-readable test scenarios
### Test Management
- Cucumber test runners with TestNG integration
- Before/After hooks for setup and teardown
- ThreadLocal WebDriver for thread safety
### WebDriver Management
- Automatic driver download via WebDriverManager
- Support for Chrome, Firefox, Edge
- Single point of driver initialization
### Logging & Reporting
- Log4j2 for console and file logging
- Cucumber HTML reports
- JSON test reports
- Allure reporting support
## TEST SCENARIO: NAUKRI JOB SEARCH
The framework includes an automated test that:
1. Navigates to https://www.naukri.com/
2. Searches for "QA Engineer" skill
3. Filters by "5 Years" experience
4. Filters by "Hyderabad" location
5. Retrieves top 10 latest job postings
6. Prints job details (title, company, posted time, apply link) to console
## OUTPUT EXAMPLE:
\\\
========== TOP 10 JOB POSTINGS ==========
Job 1:
  Title: QA Engineer
  Company: ABC Corp
  Posted: 2 hours ago
  Apply Link: https://www.naukri.com/apply/...
Job 2:
  Title: Senior QA Engineer
  Company: XYZ Solutions
  Posted: 4 hours ago
  Apply Link: https://www.naukri.com/apply/...
[... more jobs ...]
========== END OF JOB LIST ==========
Total jobs retrieved: 10
\\\
## NEXT STEPS:
1. Run Maven clean install to download dependencies
2. Execute: mvn test
3. Check target/cucumber-reports/ for reports
4. Check logs/ folder for execution logs
## TROUBLESHOOTING:
If tests fail:
- Check internet connection for WebDriver download
- Verify Java and Maven installation: java -version, mvn -version
- Update locators if website structure changes
- Increase wait times in BasePage if elements load slowly
- Ensure Chrome/Firefox is installed
## CUSTOMIZATION:
To change browser (in Hooks.java):
- DriverManager.initializeDriver("firefox"); 
- DriverManager.initializeDriver("edge");
To add new tests:
1. Create .feature file in src/test/resources/features/
2. Create page object in src/main/java/org/example/pages/
3. Create step definitions in src/test/java/org/example/stepDefinitions/
## FRAMEWORK READY! ✓
All components are set up and ready for execution. The framework follows industry best practices and is production-ready.
