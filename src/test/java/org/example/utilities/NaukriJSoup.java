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
 * Key fix: Uses the EXACT URL format Naukri generates in the browser:
 *   /qa-testing-jobs-in-hyderabad-secunderabad?k=qa testing&l=hyderabad&experience=5&jobAge=1&cityTypeGid=17
 */
public class NaukriJSoup {

    private static final String BASE_URL    = "https://www.naukri.com";
    private static final int    WAIT_SEC    = 15;
    private static final int    MAX_RETRIES = 3;

    // Naukri's internal city IDs — add more cities here as needed
    private static final Map<String, String> CITY_GID = new HashMap<>();
    static {
        CITY_GID.put("hyderabad",  "17");
        CITY_GID.put("bangalore",  "97");
        CITY_GID.put("bengaluru",  "97");
        CITY_GID.put("mumbai",     "3");
        CITY_GID.put("delhi",      "1");
        CITY_GID.put("chennai",    "127");
        CITY_GID.put("pune",       "73");
        CITY_GID.put("kolkata",    "105");
        CITY_GID.put("ahmedabad",  "69");
        CITY_GID.put("noida",      "1");
        CITY_GID.put("gurgaon",    "1");
    }

    // Naukri's URL suffix per city (how they display it in the slug)
    private static final Map<String, String> CITY_SLUG = new HashMap<>();
    static {
        CITY_SLUG.put("hyderabad",  "hyderabad-secunderabad");
        CITY_SLUG.put("bangalore",  "bangalore-bengaluru");
        CITY_SLUG.put("bengaluru",  "bangalore-bengaluru");
        CITY_SLUG.put("mumbai",     "mumbai");
        CITY_SLUG.put("delhi",      "delhi-ncr");
        CITY_SLUG.put("chennai",    "chennai");
        CITY_SLUG.put("pune",       "pune");
        CITY_SLUG.put("kolkata",    "kolkata");
        CITY_SLUG.put("ahmedabad",  "ahmedabad");
        CITY_SLUG.put("noida",      "delhi-ncr");
        CITY_SLUG.put("gurgaon",    "delhi-ncr");
    }

    /**
     * Fetch jobs using the existing Selenium driver session.
     *
     * @param keyword    e.g. "QA Automation" or "qa testing"
     * @param experience e.g. "5 years" or "5"
     * @param location   e.g. "Hyderabad"
     * @param maxJobs    max results (up to 20 per page)
     * @param jobAgeDays 1=today, 7=last week
     * @return List of maps — keys: title, company, posted, link
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
                    System.out.println("✓ Fetched " + jobs.size() + " jobs.");
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

    // ─── Core scraping ────────────────────────────────────────────────────────

    private static List<Map<String, String>> scrapeWithSelenium(
            WebDriver driver,
            String keyword, String expNum,
            String location, int maxJobs, int jobAgeDays) {

        String url = buildUrl(keyword, expNum, location, jobAgeDays);
        System.out.println("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

        // Dismiss any popup/overlay that might block job cards
        dismissPopups(driver);

        List<WebElement> jobCards = waitForJobCards(driver, wait);

        if (jobCards.isEmpty()) {
            System.out.println("No job cards found. Page title: " + driver.getTitle());
            // Log current URL — Naukri sometimes redirects
            System.out.println("Current URL after load: " + driver.getCurrentUrl());
            return new ArrayList<>();
        }

        System.out.println("Found " + jobCards.size() + " job cards.");
        return extractJobs(jobCards, maxJobs);
    }

    // ─── Build the EXACT URL Naukri uses ────────────────────────────────────

    private static String buildUrl(String keyword, String expNum,
                                   String location, int jobAgeDays) {
        String locKey      = location.toLowerCase().trim();
        String cityGid     = CITY_GID.getOrDefault(locKey, "");
        String citySlug    = CITY_SLUG.getOrDefault(locKey,
                locKey.replace(" ", "-"));

        // Keyword slug: "qa testing" -> "qa-testing"
        String kwSlug      = keyword.toLowerCase().trim().replace(" ", "-");

        // Keyword param: "qa testing" (spaces, not encoded — Naukri uses raw spaces)
        String kwParam     = keyword.trim();

        // Build URL exactly matching Naukri's browser format:
        // /qa-testing-jobs-in-hyderabad-secunderabad?k=qa testing&l=hyderabad&experience=5&jobAge=1&cityTypeGid=17&sort=1
        StringBuilder url = new StringBuilder(BASE_URL)
                .append("/").append(kwSlug).append("-jobs-in-").append(citySlug)
                .append("?k=").append(kwParam.replace(" ", "%20"))
                .append("&l=").append(locKey)
                .append("&experience=").append(expNum)
                .append("&jobAge=").append(jobAgeDays)
                .append("&sort=f"); // newest first

        if (!cityGid.isEmpty()) {
            url.append("&cityTypeGid=").append(cityGid);
        }

        return url.toString();
    }

    // ─── Dismiss cookie banners / login popups ────────────────────────────────

    private static void dismissPopups(WebDriver driver) {
        String[] closeSelectors = {
                "button[class*='close']",
                "button[class*='Cross']",
                "button[class*='dismiss']",
                "[class*='crossIcon']",
                "[class*='close-btn']",
        };
        for (String sel : closeSelectors) {
            try {
                List<WebElement> closes = driver.findElements(By.cssSelector(sel));
                if (!closes.isEmpty()) {
                    closes.get(0).click();
                    System.out.println("Dismissed popup: " + sel);
                    sleep(500);
                }
            } catch (Exception ignored) {}
        }
    }

    // ─── Wait for job cards — tries all known selectors ──────────────────────

    private static List<WebElement> waitForJobCards(WebDriver driver, WebDriverWait wait) {
        String[] selectors = {
                "article.jobTuple",
                "div.jobTuple",
                "article[class*='jobTuple']",
                "div[class*='jobTuple']",
                ".cust-job-tuple",
                "div[class*='srp-jobtuple']",
                "div[class*='job-post-container']",
                "article.job-post",
        };

        for (String selector : selectors) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(selector)));
                List<WebElement> cards = driver.findElements(By.cssSelector(selector));
                if (!cards.isEmpty()) {
                    System.out.println("Cards found using: " + selector);
                    return cards;
                }
            } catch (TimeoutException ignored) {}
        }

        // Fallback: any link with job-listings- in href
        System.out.println("All selectors timed out. Trying fallback XPath...");
        List<WebElement> fallback = driver.findElements(
                By.xpath("//a[contains(@href,'job-listings-')]"));
        System.out.println("Fallback links found: " + fallback.size());
        return fallback;
    }

    // ─── Extract job data — stale-element-safe ────────────────────────────────

    private static List<Map<String, String>> extractJobs(
            List<WebElement> jobCards, int maxJobs) {

        List<Map<String, String>> jobs = new ArrayList<>();
        int count = Math.min(maxJobs, jobCards.size());

        for (int i = 0; i < count; i++) {
            try {
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

                if (!job.get("title").isEmpty()) jobs.add(job);

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