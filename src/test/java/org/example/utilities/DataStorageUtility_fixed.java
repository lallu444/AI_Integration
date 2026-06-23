package org.example.utilities;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Handles CSV storage and run-to-run job comparison.
 *
 * CRITICAL ORDER (must always be respected):
 *   1. Call getNewJobsSinceLastRun()  ← reads old CSV, finds diff
 *   2. Call saveJobData()             ← overwrites CSV with current data
 *   3. Use returned newJobs for email decision
 *
 * This ensures we always diff against the PREVIOUS run, never the current one.
 */
public class DataStorageUtility_fixed {

    private static final String CSV_FILE = "jobs_output.csv";

    // ─────────────────────────────────────────────────────────────────────────
    // PRIMARY METHOD — call this first, before saveJobData()
    // Returns ONLY jobs that weren't in the previous CSV
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Compare current jobs against the PREVIOUS run's CSV.
     * Returns only genuinely new jobs (not seen before).
     *
     * CALL THIS BEFORE saveJobData() — otherwise you'll compare against yourself.
     *
     * @param currentJobs jobs fetched in this run
     * @return map with keys: totalCurrent, totalPrevious, totalNew,
     *         isNewData, currentJobs, newJobs
     *         (exact keys EmailUtility.createEmailBody() expects)
     */
    public static Map<String, Object> compareJobData(List<Map<String, String>> currentJobs) {
        // Read previous run's data BEFORE we overwrite
        List<Map<String, String>> previousJobs = readCsv();

        // Find jobs in current that were NOT in previous — matched by link URL
        List<Map<String, String>> newJobs = findNewJobs(currentJobs, previousJobs);

        boolean isNewData = !newJobs.isEmpty();

        System.out.println("Comparison → current: " + currentJobs.size()
                + " | previous: " + previousJobs.size()
                + " | new: "      + newJobs.size()
                + " | hasNew: "   + isNewData);

        // Build result map with EXACT keys EmailUtility expects
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCurrent",  currentJobs.size());
        result.put("totalPrevious", previousJobs.size());
        result.put("totalNew",      newJobs.size());
        result.put("isNewData",     isNewData);
        result.put("currentJobs",   currentJobs);
        result.put("newJobs",       newJobs);

        return result;
    }

    /**
     * Overwrite CSV with current run's jobs.
     * Always call this AFTER compareJobData() — never before.
     */
    public static void saveJobData(List<Map<String, String>> jobs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, false))) {
            writer.println("Title,Company,Posted,Link");
            for (Map<String, String> job : jobs) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        csv(job.getOrDefault("title",   "")),
                        csv(job.getOrDefault("company", "")),
                        csv(job.getOrDefault("posted",  "")),
                        csv(job.getOrDefault("link",    "")));
            }
            System.out.println("✓ CSV saved: " + CSV_FILE + " (" + jobs.size() + " jobs)");
        } catch (IOException e) {
            System.out.println("✗ Error saving CSV: " + e.getMessage());
        }
    }

    /**
     * Quick check — are all current jobs already in the previous CSV?
     * Used as a fast guard before doing full comparison.
     * CALL THIS BEFORE saveJobData().
     */
    public static boolean isSameAsLastRun(List<Map<String, String>> currentJobs) {
        List<Map<String, String>> previousJobs = readCsv();
        if (previousJobs.isEmpty()) return false; // first run — always treat as new

        List<Map<String, String>> newJobs = findNewJobs(currentJobs, previousJobs);
        boolean same = newJobs.isEmpty() && currentJobs.size() == previousJobs.size();
        System.out.println("isSameAsLastRun: " + same
                + " (current=" + currentJobs.size()
                + ", previous=" + previousJobs.size()
                + ", new=" + newJobs.size() + ")");
        return same;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Find jobs in current that are NOT in previous.
     * Matches by job link URL — most stable unique identifier.
     * Falls back to title+company match if link is missing.
     */
    private static List<Map<String, String>> findNewJobs(
            List<Map<String, String>> current,
            List<Map<String, String>> previous) {

        // Build a set of previously seen job fingerprints
        Set<String> seen = new HashSet<>();
        for (Map<String, String> job : previous) {
            seen.add(fingerprint(job));
        }

        List<Map<String, String>> newJobs = new ArrayList<>();
        for (Map<String, String> job : current) {
            if (!seen.contains(fingerprint(job))) {
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    /**
     * Unique fingerprint for a job — prefers link, falls back to title+company.
     * Normalised to lowercase + trimmed to avoid false mismatches.
     */
    private static String fingerprint(Map<String, String> job) {
        String link = job.getOrDefault("link", "").trim().toLowerCase();
        if (!link.isEmpty()) return "link:" + link;

        // Fallback if link is missing
        String title   = job.getOrDefault("title",   "").trim().toLowerCase();
        String company = job.getOrDefault("company", "").trim().toLowerCase();
        return "tc:" + title + "|" + company;
    }

    /** Read the previous run's CSV into a list of job maps */
    private static List<Map<String, String>> readCsv() {
        List<Map<String, String>> jobs = new ArrayList<>();
        File f = new File(CSV_FILE);
        if (!f.exists()) {
            System.out.println("No previous CSV found — treating as first run.");
            return jobs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = splitCsvLine(line);
                if (parts.length >= 4) {
                    Map<String, String> job = new HashMap<>();
                    job.put("title",   unquote(parts[0]));
                    job.put("company", unquote(parts[1]));
                    job.put("posted",  unquote(parts[2]));
                    job.put("link",    unquote(parts[3]));
                    jobs.add(job);
                }
            }
            System.out.println("Read " + jobs.size() + " jobs from previous CSV.");
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return jobs;
    }

    private static String csv(String v) {
        if (v == null) return "";
        return v.replace("\"", "\"\"");
    }

    private static String unquote(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\""))
            s = s.substring(1, s.length() - 1);
        return s.replace("\"\"", "\"");
    }

    private static String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}