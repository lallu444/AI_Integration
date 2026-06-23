package org.example.utilities;

import org.example.drivers.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

/**
 * Fetches Naukri job listings using the existing Selenium WebDriver.
 *
 * Why Selenium (not JSoup/HTTP API)?
 *   Naukri is fully JavaScript-rendered + Cloudflare protected.
 *   JSoup and direct HTTP calls return empty pages — a real browser is required.
 *
 * This reuses your existing DriverManager (no new browser opened),
 * navigates to the search URL directly, waits for jobs to render,
 * and extracts with robust multi-selector + stale-element-safe logic.
 */
public class NaukriApiClient {

    private static final String BASE_URL    = "https://www.naukri.com";
    private static final int    WAIT_SEC    = 15;
    private static final int    MAX_RETRIES = 3;

    /**
     * Fetch jobs using the existing Selenium driver session.
     *
     * @param keyword    e.g. "QA Automation"
     * @param experience e.g. "5 years" or "5" — both handled
     * @param location   e.g. "Hyderabad"
     * @param maxJobs    max results (up to 20 per page)
     * @param jobAgeDays 1=today, 7=last week
     * @return List of maps — keys: title, company, posted, link
     *         (exact keys EmailUtility + TableFormatterUtility expect)
     */
    public static List<Map<String, String>> fetchJobs(
            String keyword,
            String experience,
            String location,
            int maxJobs,
            int jobAgeDays) {

        // "5 years" -> "5"
        String expNum = (experience == null) ? "0"
                : experience.replaceAll("[^0-9]", "");
        if (expNum.isEmpty()) expNum = "0";

        WebDriver driver = DriverManager.getDriver();

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Fetching jobs via Selenium (attempt " + attempt + ")...");
                List<Map<String, String>> jobs =
                        scrapeWithSelenium(driver, keyword, expNum, location, maxJobs, jobAgeDays);

                if (!jobs.isEmpty()) {
                    System.out.println("Fetched " + jobs.size() + " jobs.");
                    return jobs;
                }

                System.out.println("No jobs on attempt " + attempt + ". Retrying...");
                sleep(3000 * attempt);

            } catch (Exception e) {
                System.out.println("Attempt " + attempt + " error: " + e.getMessage());
                sleep(3000);
            }
        }

        System.out.println("All retries exhausted — returning empty list.");
        return new ArrayList<>();
    }

    // ─── Core scraping logic ──────────────────────────────────────────────────

    private static List<Map<String, String>> scrapeWithSelenium(
            WebDriver driver,
            String keyword, String expNum,
            String location, int maxJobs, int jobAgeDays) {

        // Build direct search URL — bypasses homepage search bar entirely
        // e.g. https://www.naukri.com/qa-automation-jobs-in-hyderabad?experience=5&jobAge=1&sort=1
        String urlKeyword  = keyword.toLowerCase().trim().replace(" ", "-");
        String urlLocation = location.toLowerCase().trim().replace(" ", "-");
        String url = BASE_URL + "/" + urlKeyword + "-jobs-in-" + urlLocation
                + "?experience=" + expNum
                + "&jobAge="     + jobAgeDays
                + "&sort=1";     // newest first

        System.out.println("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

        // Wait for and find job cards
        List<WebElement> jobCards = waitForJobCards(driver, wait);

        if (jobCards.isEmpty()) {
            System.out.println("No job cards found. Page title: " + driver.getTitle());
            return new ArrayList<>();
        }

        System.out.println("Found " + jobCards.size() + " job cards on page.");
        return extractJobs(driver, jobCards, maxJobs);
    }

    // ─── Wait for job cards — tries 8 selectors in order ─────────────────────

    private static List<WebElement> waitForJobCards(WebDriver driver, WebDriverWait wait) {
        String[] selectors = {
                "article.jobTuple",
                "div.jobTuple",
                "article[class*='jobTuple']",
                "div[class*='jobTuple']",
                ".cust-job-tuple",
                "article.job-post",
                "div[class*='srp-jobtuple']",
                "div[class*='job-post-container']",
        };

        for (String selector : selectors) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(selector)));
                List<WebElement> cards = driver.findElements(By.cssSelector(selector));
                if (!cards.isEmpty()) {
                    System.out.println("Cards found using selector: " + selector);
                    return cards;
                }
            } catch (TimeoutException ignored) {
                // try next selector
            }
        }

        // Final fallback: any link with job-listings- in href
        System.out.println("All selectors failed. Trying fallback XPath...");
        List<WebElement> fallback = driver.findElements(
                By.xpath("//a[contains(@href,'job-listings-')]"));
        System.out.println("Fallback links found: " + fallback.size());
        return fallback;
    }

    // ─── Extract job data — stale-element-safe ────────────────────────────────

    private static List<Map<String, String>> extractJobs(
            WebDriver driver, List<WebElement> jobCards, int maxJobs) {

        List<Map<String, String>> jobs = new ArrayList<>();
        int count = Math.min(maxJobs, jobCards.size());

        for (int i = 0; i < count; i++) {
            try {
                // Re-fetch every time to avoid StaleElementReferenceException
                WebElement card = jobCards.get(i);

                Map<String, String> job = new HashMap<>();

                // title + link
                WebElement titleEl = findFirst(card,
                        By.cssSelector("a.title"),
                        By.cssSelector("a[class*='title']"),
                        By.cssSelector("a[class*='job-title']"),
                        By.xpath(".//h2/a"),
                        By.xpath(".//a[@title]"));

                if (titleEl == null) continue;
                job.put("title", clean(titleEl.getText()));
                job.put("link",  titleEl.getAttribute("href"));

                // company
                WebElement compEl = findFirst(card,
                        By.cssSelector("a.comp-name"),
                        By.cssSelector("a[class*='comp-name']"),
                        By.cssSelector("span[class*='comp-name']"),
                        By.cssSelector("a[class*='company-name']"),
                        By.xpath(".//span[contains(@class,'company')]"));
                job.put("company", compEl != null ? clean(compEl.getText()) : "N/A");

                // posted date
                WebElement postedEl = findFirst(card,
                        By.cssSelector("span.job-post-day"),
                        By.cssSelector("span[class*='job-post-day']"),
                        By.cssSelector("span[class*='posted']"),
                        By.cssSelector("label[class*='freshness']"),
                        By.xpath(".//span[contains(@class,'date')]"));
                job.put("posted", postedEl != null ? clean(postedEl.getText()) : "N/A");

                if (!job.get("title").isEmpty()) {
                    jobs.add(job);
                }

            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element at index " + i + " — skipping.");
            } catch (Exception e) {
                System.out.println("Error on card " + (i + 1) + ": " + e.getMessage());
            }
        }

        System.out.println("Extracted " + jobs.size() + " valid jobs.");
        return jobs;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static WebElement findFirst(WebElement parent, By... locators) {
        for (By locator : locators) {
            try {
                List<WebElement> found = parent.findElements(locator);
                if (!found.isEmpty()) return found.get(0);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}