package org.example.utilities;

import com.google.gson.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.net.URI;
import java.net.http.*;
import java.util.*;

/**
 * Calls Naukri's own internal JSON search API.
 * No browser, no Selenium, no ChromeDriver needed.
 * Runs in ~2 seconds vs ~60 seconds with Selenium.
 */
public class NaukriApiClientOLD {

    private static final String API_BASE =
        "https://www.naukri.com/jobapi/v3/search";
    private static final int MAX_RETRIES = 3;
    //RequestSpecification request;

    /**
     * Fetch jobs from Naukri API.
     *
     * @param keyword    e.g. "QA Automation"
     * @param experience e.g. "5 years" OR "5" — both handled
     * @param location   e.g. "Hyderabad"
     * @param maxJobs    max results (up to 20 per page)
     * @param jobAgeDays freshness: 1 = today, 7 = last week
     * @return List of job maps with keys: title, company, posted, link
     *         (matches your existing TableFormatterUtility and EmailUtility exactly)
     */
    public static List<Map<String, String>> fetchJobs(
            String keyword,
            String experience,
            String location,
            int maxJobs,
            int jobAgeDays) {

        // Strip non-numeric chars: "5 years" -> "5", "5" -> "5"
        String expNumber = experience == null ? "0"
                         : experience.replaceAll("[^0-9]", "");
        if (expNumber.isEmpty()) expNumber = "0";

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Calling Naukri API (attempt " + attempt + ")...");
//                List<Map<String, String>> jobs = callApi(
//                    keyword, expNumber, location, maxJobs, jobAgeDays);
                List<Map<String, String>> jobs = getData(
                        keyword, expNumber, location, maxJobs, jobAgeDays);

                if (!jobs.isEmpty()) {
                    System.out.println("✓ API returned " + jobs.size() + " jobs.");
                    return jobs;
                }

                System.out.println("Empty response on attempt " + attempt + ". Retrying...");
                Thread.sleep(2000L * attempt); // backoff: 2s, 4s, 6s

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.out.println("API attempt " + attempt + " failed: " + e.getMessage());
            }
        }

        System.out.println("All retries exhausted. Returning empty list.");
        return new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private: Build URL and call API
    // ─────────────────────────────────────────────────────────────────────────

    private static List<Map<String, String>> callApi(
            String keyword, String experience,
            String location, int maxJobs, int jobAgeDays)
            throws Exception {

        // URL-encode spaces
        String encKeyword  = keyword.trim().replace(" ", "%20");
        String encLocation = location.trim().replace(" ", "%20");

        String url = API_BASE
            + "?noOfResults=" + maxJobs
            + "&urlType=search_by_keyword"
            + "&searchType=adv"
            + "&keyword="    + encKeyword
            + "&experience=" + experience
            + "&location="   + encLocation
            + "&jobAge="     + jobAgeDays
            + "&sort=1"          // sort by date — newest first
            + "&pageno=1";

        System.out.println("Request URL: " + url);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            // Headers that mimic a real Chrome browser on Windows
            .header("Accept",               "application/json")
            .header("Content-Type",         "application/json")
            .header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/124.0.0.0 Safari/537.36")
            .header("Referer",              "https://www.naukri.com/")
            .header("appid",                "109")
            .header("systemid",             "Naukri")
            .header("x-http-method-override", "GET")
            .GET()
            .build();

        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status: " + response.statusCode());

        if (response.statusCode() != 200) {
            System.out.println("Non-200 response. Body preview: "
                + response.body().substring(0, Math.min(400, response.body().length())));
            return new ArrayList<>();
        }

        return parseResponse(response.body());
    }

    public static List<Map<String, String>> getData(
            String keyword, String experience,
            String location, int maxJobs, int jobAgeDays)
            throws Exception {
        // URL-encode spaces
        String encKeyword  = keyword.trim().replace(" ", "%20");
        String encLocation = location.trim().replace(" ", "%20");

        String url = API_BASE
                + "?noOfResults=" + maxJobs
                + "&urlType=search_by_keyword"
                + "&searchType=adv"
                + "&keyword="    + encKeyword
                + "&experience=" + experience
                + "&location="   + encLocation
                + "&jobAge="     + jobAgeDays
                + "&sort=1"          // sort by date — newest first
                + "&pageno=1";

        System.out.println("Request URL: " + url);
        Response response=sendGet(url);
        System.out.println(response.prettyPrint());
        return parseResponse(response.getBody().asString());

    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private: Parse JSON → List<Map<String, String>>
    // Keys returned: title, company, posted, link
    // (exactly what TableFormatterUtility and EmailUtility expect)
    // ─────────────────────────────────────────────────────────────────────────

    private static List<Map<String, String>> parseResponse(String json) {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (!root.has("jobDetails")) {
                System.out.println("'jobDetails' key not found. Available keys: "
                    + root.keySet());
                return jobs;
            }

            JsonArray jobDetails = root.getAsJsonArray("jobDetails");
            System.out.println("Jobs in API response: " + jobDetails.size());

            for (JsonElement element : jobDetails) {
                try {
                    JsonObject job = element.getAsJsonObject();
                    Map<String, String> jobMap = new HashMap<>();

                    // ── title ──────────────────────────────────────────────
                    jobMap.put("title", safeString(job, "title"));

                    // ── company ────────────────────────────────────────────
                    // Naukri nests company name differently across API versions
                    String company = safeString(job, "companyName");
                    if (company.isEmpty() && job.has("company")
                            && job.get("company").isJsonObject()) {
                        company = safeString(job.getAsJsonObject("company"), "label");
                    }
                    jobMap.put("company", company);

                    // ── posted ─────────────────────────────────────────────
                    // "footerPlaceholderLabel" = "3 Days Ago", "Today", etc.
                    String posted = safeString(job, "footerPlaceholderLabel");
                    if (posted.isEmpty()) posted = safeString(job, "createdDate");
                    if (posted.isEmpty()) posted = "N/A";
                    jobMap.put("posted", posted);

                    // ── link ───────────────────────────────────────────────
                    String link = safeString(job, "jdURL");
                    if (link.isEmpty()) link = safeString(job, "staticUrl");
                    if (!link.isEmpty() && !link.startsWith("http")) {
                        link = "https://www.naukri.com" + link;
                    }
                    jobMap.put("link", link);

                    // Only add jobs that have at least title + company
                    if (!jobMap.get("title").isEmpty()
                            && !jobMap.get("company").isEmpty()) {
                        jobs.add(jobMap);
                    }

                } catch (Exception e) {
                    System.out.println("Skipping one malformed job entry: "
                        + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to parse API response: " + e.getMessage());
            System.out.println("Response preview: "
                + json.substring(0, Math.min(500, json.length())));
        }
        return jobs;
    }



    // ─────────────────────────────────────────────────────────────────────────
    // Helper: null-safe string extraction from JsonObject
    // ─────────────────────────────────────────────────────────────────────────

    private static String safeString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return "";
        JsonElement el = obj.get(key);
        if (el.isJsonPrimitive()) return el.getAsString().trim();
        return ""; // skip nested objects — caller handles those
    }
    public static Response sendGet(String path) {
        RequestSpecification request = RestAssured.given().baseUri(path).header("appid", "109").header("systemid", "Naukri");
        return request.when().get(path);
    }
}
