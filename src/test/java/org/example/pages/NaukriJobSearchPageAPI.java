package org.example.pages;

import org.example.utilities.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NaukriJobSearchPageAPI extends BasePage {

    // ─── Locators (kept for non-job-search Selenium features) ────────────────
    private By searchSkillsInput = By.xpath("//input[contains(@placeholder,'Enter skill')]");
    private By experienceDropdown = By.xpath("//div[contains(@class,'dropdownMainContainer')]");
    private By locationInput = By.xpath("//input[@placeholder='Enter location']");
    private By searchButton = By.xpath("//div[contains(text(), 'Search')]");
    private By jobListings = By.xpath("//div[contains(@class,'jobtuple')]");
    private By jobTitle = By.xpath(".//a[contains(@class,'title')]");
    private By companyName = By.xpath(".//a[contains(@class,'comp-name')]");
    private By jobPostedTime = By.xpath(".//span[contains(@class,'job-post-day')]");
    private By applyLink = By.xpath(".//a[contains(@href, 'apply')]");
    private By experienceFilter = By.xpath("//div[@data-testid='experienceFilter']//span[@title='5 years']");
    private By locationFilter = By.xpath("//div[@data-testid='locationFilter']//span[contains(text(), 'Hyderabad')]");
    private By freshnessFilter = By.xpath("//div[@data-testid='freshnessFilter']//span[contains(text(), 'Last 1 day')]");
    private By sortByDateButton = By.xpath("//div[contains(@class,'sort')]//span[contains(text(), 'Date')]");
    private By FreshnessList = By.xpath("//ul[@data-filter-id='freshness']");

    @FindBy(xpath = "//a[@title='Jobseeker Login']") public WebElement loginBtn;
    @FindBy(xpath = "//label[@class='label']//following-sibling::input[@type='text']") public WebElement EmailInput;
    @FindBy(xpath = "//label[@class='label']//following-sibling::input[@type='password']") public WebElement PassowrdInput;
    @FindBy(xpath = "//div[contains(@class,'crossIcon')]") public WebElement chatBotDialog;
    @FindBy(xpath = "//img[@alt='naukri user profile img']") public WebElement ProfileImg;
    @FindBy(xpath = "//button[contains(@class,'btn-primary loginButton')]") public WebElement loginSubmitBtn;
    @FindBy(xpath = "//div[@class='view-profile-wrapper']/a") public WebElement viewProfileBtn;
    @FindBy(xpath = "//div[@class='profilePage']") public WebElement profilePage;
    @FindBy(xpath = "//span[text()='Resume headline']//following-sibling::span") public WebElement resumeHeadlineEdit;
    @FindBy(xpath = "//form[@name='resumeHeadlineForm']") public WebElement resumeHeadlinePopup;
    @FindBy(xpath = "//form//button[text()='Save']") public WebElement resumeHeadlineSaveBtn;
    @FindBy(xpath = "//span[@class='typ-14Medium mod-date-val']") public WebElement updateConfirmation;

    public NaukriJobSearchPageAPI(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ─── Navigation & Search (kept for @Test and @MailReport scenarios) ───────

    public void navigateToNaukri() {
        driver.navigate().to("https://www.naukri.com/");
        driver.manage().window().maximize();
    }

    public void searchSkills(String skill) throws IOException {
        sendKeys(searchSkillsInput, skill);
    }

    public void selectExperience(String experience) {
        click(experienceDropdown);
        WebElement selection = waitForElementToBeVisible(By.xpath(
            "//div[contains(@class,'dropDownPrimaryContainer')]//span[text()='"
            + experience + "']//ancestor::li"));
        scrollToElement(selection);
        clickJavascript(selection);
    }

    public void enterLocation(String location) throws IOException {
        sendKeys(locationInput, location);
    }

    public void clickSearchButton() {
        click(searchButton);
    }

    // ─── Selenium-based filter methods (kept for @Test scenario) ─────────────

    public void filterByExperience(String yearsOfExperience) {
        try {
            threadSleep(2000);
            By experienceCheckbox = By.xpath(
                "//span[contains(text(), '" + yearsOfExperience + "')]"
                + "/ancestor::label//input");
            WebElement expCheckbox = waitForElementToBeVisible(experienceCheckbox);
            if (!expCheckbox.isSelected()) {
                clickJavascript(expCheckbox);
                System.out.println("Filtered by experience: " + yearsOfExperience);
            }
        } catch (Exception e) {
            System.out.println("Error filtering by experience: " + e.getMessage());
        }
    }

    public void filterByLocation(String location) {
        try {
            threadSleep(2000);
            // Fixed: use input[type='checkbox'] instead of 'i' tag
            By locationCheckbox = By.xpath(
                "//span[contains(text(), '" + location + "')]"
                + "/ancestor::label//input[@type='checkbox']");
            WebElement locCheckbox = waitForElementToBeVisible(locationCheckbox);
            if (!locCheckbox.isSelected()) {
                clickJavascript(locCheckbox);
                System.out.println("Filtered by location: " + location);
            }
        } catch (Exception e) {
            System.out.println("Error filtering by location: " + e.getMessage());
        }
    }

    public void filterByFreshness(String freshness) {
        try {
            threadSleep(2000);
            By freshnessCheckbox = By.xpath(
                "//div[@data-type='select']/div[@data-filter-id='freshness']");
            WebElement freshCheckbox = waitForElementToBeVisible(freshnessCheckbox);
            scrollToElement(freshCheckbox);
            click(freshCheckbox);
            waitForElementToBeVisible(FreshnessList);
            WebElement Freshness = driver.findElement(By.xpath(
                "//ul[@data-filter-id='freshness']//li[contains(@title,'" + freshness + "')]"));
            click(Freshness);
            System.out.println("Filtered by freshness: " + freshness);
        } catch (Exception e) {
            System.out.println("Error filtering by freshness: " + e.getMessage());
        }
    }

    public void sortByDate() {
        try {
            threadSleep(2000);
            By sortDropdown = By.xpath(
                "//span[contains(@class, 'sort-by-container')]//button");
            WebElement sortDropdownElement = waitForElementToBeVisible(sortDropdown);
            scrollToElement(sortDropdownElement);
            clickjavascript(sortDropdownElement);
            By dateSortOption = By.xpath("//li[contains(@title, 'Date')]");
            threadSleep(1000);
            click(dateSortOption);
            System.out.println("Sorted by Date");
        } catch (Exception e) {
            System.out.println("Error sorting by date: " + e.getMessage());
        }
    }

    public void filterJobListings() {
        try {
            System.out.println("Starting job listing filters...");
            threadSleep(3000);
            filterByLocation("Hyderabad");
            threadSleep(1500);
            filterByFreshness("Last 1 day");
            threadSleep(1500);
            sortByDate();
            threadSleep(3000);
            List<WebElement> jobElements = driver.findElements(jobListings);
            System.out.println("Total job listings after filtering: " + jobElements.size());
        } catch (Exception e) {
            System.out.println("Error filtering job listings: " + e.getMessage());
        }
    }

    // ─── Selenium DOM scraping (kept for @Test scenario) ─────────────────────

    public List<Map<String, String>> getTop10JobsAsDataFromDom() {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            Thread.sleep(3000);
            List<WebElement> jobElements = driver.findElements(jobListings);
            int count = Math.min(10, jobElements.size());
            for (int i = 0; i < count; i++) {
                // Re-fetch list each iteration to avoid StaleElementReferenceException
                jobElements = driver.findElements(jobListings);
                WebElement job = jobElements.get(i);
                try {
                    Map<String, String> jobData = new HashMap<>();
                    jobData.put("title",   job.findElement(jobTitle).getText());
                    jobData.put("company", job.findElement(companyName).getText());
                    jobData.put("posted",  job.findElement(jobPostedTime).getText());
                    jobData.put("link",    job.findElement(jobTitle).getAttribute("href"));
                    jobs.add(jobData);
                } catch (Exception e) {
                    System.out.println("Error fetching DOM job " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting DOM jobs: " + e.getMessage());
        }
        return jobs;
    }

    // ─── API-based job fetching (used by @MailReport and @MailReportPropertyReader) ─

    /**
     * Fetch jobs via Naukri's internal API — fast, reliable, no browser needed.
     * Keys returned: title, company, posted, link
     * (exactly what TableFormatterUtility and EmailUtility expect)
     */
    public List<Map<String, String>> getTop10JobsAsData() {
        System.out.println("\n🔍 Fetching jobs via Naukri API...");
//        return NaukriApiClient.fetchJobs(
//            Property.Role,
//            Property.Experince,
//            Property.Location,
//            10,   // max jobs
//            1     // last 1 day
//        );
        return NaukriJSoup.fetchJobs(
                Property.Role,
                Property.Experince,
                Property.Location,
                10,   // max jobs
                1     // last 1 day
        );
    }

    // ─── Formatting helpers ───────────────────────────────────────────────────

    public List<String> getTop10ApplyLinks() {
        List<String> applyLinks = new ArrayList<>();
        List<Map<String, String>> jobs = getTop10JobsAsDataFromDom(); // uses DOM for @Test
        for (int i = 0; i < jobs.size(); i++) {
            Map<String, String> job = jobs.get(i);
            applyLinks.add(String.format(
                "Job %d:\n  Title: %s\n  Company: %s\n  Posted: %s\n  Apply Link: %s\n",
                (i + 1),
                job.get("title"), job.get("company"),
                job.get("posted"), job.get("link")));
        }
        return applyLinks;
    }

    public void printJobsInTableFormat(List<Map<String, String>> jobs) {
        System.out.println(TableFormatterUtility.formatJobsAsTable(jobs));
    }

    // ─── Core workflow (used by @MailReport and @MailReportPropertyReader) ────

    /**
     * Full flow:
     *  1. Fetch jobs via API
     *  2. Guard: skip if empty
     *  3. Guard: skip if same as last run
     *  4. Overwrite CSV
     *  5. Send email with diff
     */
    public void executeCompleteWorkflow(String skill, String experience,
                                        String location, String recipientEmail) {
        try {
            System.out.println("\n========== WORKFLOW STARTED ==========");
            System.out.println("Skill: " + skill
                + " | Exp: " + experience
                + " | Location: " + location);

            // Step 1 — Fetch via API (fast, no browser)
//            List<Map<String, String>> currentJobs = NaukriApiClient.fetchJobs(
//                skill, experience, location, 10, 1);
            List<Map<String, String>> currentJobs = NaukriJSoup.fetchJobs(
                    skill, experience, location, 10, 1);

            // Step 2 — Guard: empty
            if (currentJobs.isEmpty()) {
                System.out.println("⚠ No jobs found — skipping CSV write and email.");
                System.out.println("========== WORKFLOW ENDED ==========\n");
                return;
            }

            // Step 3 — Guard: same as last run (check BEFORE overwriting CSV/hash)
            boolean sameAsLastRun = DataStorageUtility.isSameAsLastRun(currentJobs);

            // Step 4 — Always overwrite CSV with latest snapshot
            DataStorageUtility.saveJobData(currentJobs);

            // Step 5 — Print to console always
            printJobsInTableFormat(currentJobs);

            // Step 6 — Guard: skip email if unchanged
            if (sameAsLastRun) {
                System.out.println("⚠ Data unchanged since last run — skipping email.");
                System.out.println("========== WORKFLOW ENDED ==========\n");
                return;
            }

            // Step 7 — Build comparison for email (also saves new hash)
            Map<String, Object> comparison = DataStorageUtility.compareJobData(currentJobs);
            System.out.println(TableFormatterUtility.getSummaryStats(comparison));

            // Step 8 — Send email
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String subject   = "Job Alert - " + Property.Role + " - " + timestamp;
            String emailBody = EmailUtility.createEmailBody(comparison, timestamp);

            System.out.println("\n📧 Sending email to: " + recipientEmail);
            boolean sent = EmailUtility.sendJobDataEmail(recipientEmail, subject, emailBody);
            System.out.println(sent ? "✓ Email sent!" : "✗ Email failed.");
            System.out.println("========== WORKFLOW ENDED ==========\n");

        } catch (Exception e) {
            System.out.println("Error in workflow: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Legacy method kept for backward compatibility.
     * Now delegates to API-based path.
     */
    public List<Map<String, String>> getAndProcessJobData() {
        List<Map<String, String>> currentJobs = getTop10JobsAsData();

        if (currentJobs.isEmpty()) {
            System.out.println("⚠ No jobs found!");
            return currentJobs;
        }

        Map<String, Object> comparison = DataStorageUtility.compareJobData(currentJobs);
        DataStorageUtility.saveJobData(currentJobs);
        System.out.println(TableFormatterUtility.getSummaryStats(comparison));

        @SuppressWarnings("unchecked")
        List<Map<String, String>> newJobs =
            (List<Map<String, String>>) comparison.get("newJobs");

        return (!newJobs.isEmpty()) ? newJobs : currentJobs;
    }

    /**
     * Legacy email sender kept for backward compatibility.
     * Now uses the clean guard logic.
     */
    public boolean sendFinalDataViaEmail(String recipientEmail) {
        try {
            List<Map<String, String>> currentJobs = getTop10JobsAsData();

            if (currentJobs.isEmpty()) {
                System.out.println("⚠ No jobs found — skipping email.");
                return false;
            }

            boolean sameAsLastRun = DataStorageUtility.isSameAsLastRun(currentJobs);
            DataStorageUtility.saveJobData(currentJobs);

            if (sameAsLastRun) {
                System.out.println("⚠ Data unchanged — skipping email.");
                return false;
            }

            Map<String, Object> comparison = DataStorageUtility.compareJobData(currentJobs);
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String subject   = "Job Search Report - " + Property.Role + " - " + timestamp;
            String emailBody = EmailUtility.createEmailBody(comparison, timestamp);

            System.out.println("\nSending email to: " + recipientEmail);
            boolean sent = EmailUtility.sendJobDataEmail(recipientEmail, subject, emailBody);
            System.out.println(sent ? "✓ Email sent!" : "✗ Email failed.");
            return sent;

        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ─── Login / Profile (Selenium — kept unchanged) ─────────────────────────

    public void loginToSite(String UserName, String Password) {
        waitForElementToBeClickable(loginBtn);
        click(loginBtn);
        waitForElementToBeClickable(EmailInput);
        sendKeys(EmailInput, UserName);
        waitForElementToBeClickable(PassowrdInput);
        sendKeys(PassowrdInput, Password);
        click(loginSubmitBtn);
        try {
            waitForElementToBeVisible(chatBotDialog, 5);
            if (chatBotDialog.isDisplayed()) {
                click(chatBotDialog);
            }
            System.out.println("Closed the Chat Bot dialog");
        } catch (Exception e) {
            System.out.println("There is no chat bot dialog");
        }
        try {
            waitForElementToBeVisible(ProfileImg, 20);
            if (ProfileImg.isDisplayed()) {
                System.out.println("User logged in successfully!");
            }
        } catch (Exception e) {
            System.out.println("Login failed! " + e);
            Assert.fail("There is no user logged in!");
        }
    }

    public void updateProfile() {
        click(viewProfileBtn);
        waitForElementToBeVisible(profilePage);
        waitForElementToBeVisible(resumeHeadlineEdit, 10);
        scrollToElement(resumeHeadlineEdit);
        clickjavascript(resumeHeadlineEdit);
        waitForElementToBeVisible(resumeHeadlinePopup);
        click(resumeHeadlineSaveBtn);
        waitForElementToBeVisible(updateConfirmation);
        scrollToElement(updateConfirmation);
        Assert.assertEquals(updateConfirmation.getText(), "Today",
            "Profile Updated As Expected");
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void threadSleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void clickJavascript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
