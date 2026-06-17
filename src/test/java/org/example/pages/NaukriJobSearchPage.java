package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.example.utilities.DataStorageUtility;
import org.example.utilities.TableFormatterUtility;
import org.example.utilities.EmailUtility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NaukriJobSearchPage extends BasePage {
    // Locators for search and filters
    private By searchSkillsInput = By.xpath("//input[contains(@placeholder,'Enter skill')]");
    private By experienceDropdown = By.xpath("//div[contains(@class,'dropdownMainContainer')]");
    private By locationInput = By.xpath("//input[@placeholder='Enter location']");
    private By searchButton = By.xpath("//div[contains(text(), 'Search')]");
    private By jobListings = By.xpath("//div[contains(@class,'jobtuple')]");
    private By jobTitle = By.xpath(".//a[contains(@class,'title')]");
    private By companyName = By.xpath(".//a[contains(@class,'comp-name')]");
    private By jobPostedTime = By.xpath(".//span[contains(@class,'job-post-day')]");
    private By applyLink = By.xpath(".//a[contains(@href, 'apply')]");
    // Filter locators based on Naukri URL structure
    private By experienceFilter = By.xpath("//div[@data-testid='experienceFilter']//span[@title='5 years']");
    private By locationFilter = By.xpath("//div[@data-testid='locationFilter']//span[contains(text(), 'Hyderabad')]");
    private By freshnessFilter = By.xpath("//div[@data-testid='freshnessFilter']//span[contains(text(), 'Last 1 day')]");
    private By sortByDateButton = By.xpath("//div[contains(@class,'sort')]//span[contains(text(), 'Date')]");
    private By FreshnessList = By.xpath("//ul[@data-filter-id='freshness']");

    public NaukriJobSearchPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToNaukri() {
        driver.navigate().to("https://www.naukri.com/");
    }

    public void searchSkills(String skill) {
        sendKeys(searchSkillsInput, skill);
    }

    public void selectExperience(String experience) {
        click(experienceDropdown);
        WebElement selection = waitForElementToBeVisible(By.xpath("//div[contains(@class,'dropDownPrimaryContainer')]//span[text()='" + experience + "']//ancestor::li"));
        scrollToElement(selection);
        clickJavascript(selection);
    }

    public void enterLocation(String location) {
        sendKeys(locationInput, location);
    }

    public void clickSearchButton() {
        click(searchButton);
    }

    // Filter by experience - 5 years
    public void filterByExperience(String yearsOfExperience) {
        try {
            threadSleep(2000);
            By experienceCheckbox = By.xpath("//span[contains(text(), '" + yearsOfExperience + "')]/ancestor::label//input");
            WebElement expCheckbox = waitForElementToBeVisible(experienceCheckbox);
            if (!expCheckbox.isSelected()) {
                clickJavascript(expCheckbox);
                System.out.println("Filtered by experience: " + yearsOfExperience);
            }
        } catch (Exception e) {
            System.out.println("Error filtering by experience: " + e.getMessage());
        }
    }

    // Filter by location - Hyderabad
    public void filterByLocation(String location) {
        try {
            threadSleep(2000);
            By locationCheckbox = By.xpath("//span[contains(text(), '" + location + "')]/ancestor::label//i");
            WebElement locCheckbox = waitForElementToBeVisible(locationCheckbox);
            if (!locCheckbox.isSelected()) {
                clickJavascript(locCheckbox);
                System.out.println("Filtered by location: " + location);
            }
        } catch (Exception e) {
            System.out.println("Error filtering by location: " + e.getMessage());
        }
    }

    // Filter by freshness - Last 1 day
    public void filterByFreshness(String freshness) {
        try {
            threadSleep(2000);
            By freshnessCheckbox = By.xpath("//div[@data-type='select']/div[@data-filter-id='freshness']");
            WebElement freshCheckbox = waitForElementToBeVisible(freshnessCheckbox);
            scrollToElement(freshCheckbox);
            click(freshCheckbox); // Click to open dropdown
            waitForElementToBeVisible(FreshnessList);
//            WebElement freshnessWebElement= FreshnessList;
            WebElement Freshness = driver.findElement(By.xpath("//ul[@data-filter-id='freshness']//li[contains(@title,'" + freshness + "')]"));
            click(Freshness);
            if (!freshCheckbox.getAttribute("title").equalsIgnoreCase(freshness)) {
                System.out.println("Filtered by freshness: " + freshness);
            }
        } catch (Exception e) {
            System.out.println("Error filtering by freshness: " + e.getMessage());
        }
    }

    // Sort by date
    public void sortByDate() {
        try {
            threadSleep(2000);
            // Click on sort dropdown
            By sortDropdown = By.xpath("//span[contains(@class, 'sort-by-container')]//button");
            WebElement sortDropdownElement = waitForElementToBeVisible(sortDropdown);
            scrollToElement(sortDropdownElement);
            clickjavascript(sortDropdownElement);
            // Select Date sort option
            By dateSortOption = By.xpath("//li[contains(@title, 'Date')]");
            threadSleep(1000);
            click(dateSortOption);
            System.out.println("Sorted by Date");
        } catch (Exception e) {
            System.out.println("Error sorting by date: " + e.getMessage());
        }
    }

    // Main filter method
    public void filterJobListings() {
        try {
            System.out.println("Starting job listing filters...");
            threadSleep(3000);
//            filterByExperience("5 years");
//            threadSleep(1500);
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

    // Get top 10 jobs and return as structured data
    public List<Map<String, String>> getTop10JobsAsData() {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            Thread.sleep(3000);
            List<WebElement> jobElements = driver.findElements(jobListings);
            int count = Math.min(10, jobElements.size());
            for (int i = 0; i < count; i++) {
                WebElement job = jobElements.get(i);
                try {
                    Map<String, String> jobData = new HashMap<>();
                    jobData.put("title", job.findElement(jobTitle).getText());
                    jobData.put("company", job.findElement(companyName).getText());
                    jobData.put("posted", job.findElement(jobPostedTime).getText());
                    jobData.put("link", job.findElement(jobTitle).getAttribute("href"));
                    jobs.add(jobData);
                } catch (Exception e) {
                    System.out.println("Error fetching job " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting jobs: " + e.getMessage());
        }
        return jobs;
    }

    // Get top 10 jobs as formatted table
    public List<String> getTop10ApplyLinks() {
        List<String> applyLinks = new ArrayList<>();
        List<Map<String, String>> jobs = getTop10JobsAsData();
        for (int i = 0; i < jobs.size(); i++) {
            Map<String, String> job = jobs.get(i);
            String jobInfo = String.format(
                    "Job %d:\n" +
                            "  Title: %s\n" +
                            "  Company: %s\n" +
                            "  Posted: %s\n" +
                            "  Apply Link: %s\n",
                    (i + 1), job.get("title"), job.get("company"), job.get("posted"), job.get("link")
            );
            applyLinks.add(jobInfo);
        }
        return applyLinks;
    }

    // Print jobs in table format
    public void printJobsInTableFormat(List<Map<String, String>> jobs) {
        System.out.println(TableFormatterUtility.formatJobsAsTable(jobs));
    }

    // Get jobs, store data, compare and return final data
    public List<Map<String, String>> getAndProcessJobData() {
        List<Map<String, String>> currentJobs = getTop10JobsAsData();
        if (currentJobs.isEmpty()) {
            System.out.println("No jobs found!");
            return currentJobs;
        }
        // Save current data
        DataStorageUtility.saveJobData(currentJobs);
        // Compare with previous data
        Map<String, Object> comparison = DataStorageUtility.compareJobData(currentJobs);
        // Print summary
        System.out.println(TableFormatterUtility.getSummaryStats(comparison));
        // Determine final data
        @SuppressWarnings("unchecked")
        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
        if ((Boolean) comparison.get("isNewData") && !newJobs.isEmpty()) {
            System.out.println("\n✓ New data found! Using new jobs as final data.");
            return newJobs;
        } else {
            System.out.println("\n✓ No new data. Using all current jobs as final data.");
            return currentJobs;
        }
    }
     //Get jobs, store data, compare and return bundled data
//    public Map<String, Object> getAndProcessJobData() {
//        List<Map<String, String>> currentJobs = getTop10JobsAsData();
//        Map<String, Object> response = new HashMap<>();
//
//        if (currentJobs.isEmpty()) {
//            System.out.println("No jobs found!");
//            response.put("newJobs", new ArrayList<>());
//            response.put("oldJobs", currentJobs);
//            return response;
//        }
//
//        // Save current data
//        DataStorageUtility.saveJobData(currentJobs);
//
//        // Compare with previous data
//        Map<String, Object> comparison = DataStorageUtility.compareJobData(currentJobs);
//
//        // Print summary
//        System.out.println(TableFormatterUtility.getSummaryStats(comparison));
//
//        // Determine final data
//        @SuppressWarnings("unchecked")
//        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
//
//        if ((Boolean) comparison.get("isNewData") && !newJobs.isEmpty()) {
//            System.out.println("\n✓ New data found! Returning new and old jobs.");
//            response.put("newJobs", newJobs);
//            response.put("oldJobs", currentJobs); // Current jobs named as old jobs
//            return response;
//        } else {
//            System.out.println("\n✓ No new data. Returning empty new jobs and current jobs.");
//            response.put("newJobs", new ArrayList<>());
//            response.put("oldJobs", currentJobs);
//            return response;
//        }
//    }

    // Send final data via email
    public boolean sendFinalDataViaEmail(String recipientEmail) {
        try {
            List<Map<String, String>> finalData = getAndProcessJobData();
            Map<String, Object> comparison = DataStorageUtility.compareJobData(finalData);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String emailBody = EmailUtility.createEmailBody(comparison, timestamp);
            String subject = "📧 Naukri Job Search Report - " + timestamp;
            System.out.println("\nSending email to: " + recipientEmail);
            boolean emailSent = EmailUtility.sendJobDataEmail(recipientEmail, subject, emailBody);
            if (emailSent) {
                System.out.println("✓ Email sent successfully!");
            } else {
                System.out.println("✗ Failed to send email.");
            }
            return emailSent;
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Complete workflow: search, filter, process and send
    public void executeCompleteWorkflow(String skill, String experience, String location, String recipientEmail) {
        try {
            navigateToNaukri();
            threadSleep(2000);
            searchSkills(skill);
            threadSleep(1000);
            selectExperience(experience);
            threadSleep(1000);
            enterLocation(location);
            threadSleep(1000);
            clickSearchButton();
            threadSleep(3000);
            filterJobListings();
            List<Map<String, String>> finalData = getAndProcessJobData();
            printJobsInTableFormat(finalData);
            System.out.println("\n📧 Sending final data via email...");
            sendFinalDataViaEmail(recipientEmail);
            System.out.println("\n✓ Complete workflow executed successfully!");
        } catch (Exception e) {
            System.out.println("Error in workflow: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method for thread sleep
    private void threadSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // JavaScript click helper
    private void clickJavascript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}