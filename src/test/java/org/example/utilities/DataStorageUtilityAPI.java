package org.example.utilities;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Handles CSV storage and run-to-run comparison.
 *
 * Behaviour:
 *  - Single CSV file (jobs_output.csv) — always OVERWRITTEN, never appended.
 *  - Hash file (jobs_hash.txt) stores a fingerprint of the last run's data.
 *  - compareJobData() returns a map whose keys match EXACTLY what
 *    EmailUtility.createEmailBody() and TableFormatterUtility.getSummaryStats()
 *    expect:  totalCurrent, totalPrevious, totalNew, isNewData, currentJobs, newJobs
 */
public class DataStorageUtilityAPI {

    // Both files live in the project root (next to pom.xml)
    private static final String CSV_FILE  = "jobs_output.csv";
    private static final String HASH_FILE = "jobs_hash.txt";

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Overwrite CSV with the current run's jobs.
     * Called AFTER comparison so the old CSV is still readable during comparison.
     */
    public static void saveJobData(List<Map<String, String>> jobs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, false))) {
            // Header
            writer.println("Title,Company,Posted,Link");
            // Rows
            for (Map<String, String> job : jobs) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    csv(job.getOrDefault("title",   "")),
                    csv(job.getOrDefault("company", "")),
                    csv(job.getOrDefault("posted",  "")),
                    csv(job.getOrDefault("link",    "")));
            }
            System.out.println("✓ CSV overwritten: " + CSV_FILE
                + " (" + jobs.size() + " jobs)");
        } catch (IOException e) {
            System.out.println("✗ Error saving CSV: " + e.getMessage());
        }
    }

    /**
     * Check if current jobs are identical to the last run — using MD5 hash.
     * Call this BEFORE saveJobData() so the hash file still holds last run's value.
     */
    public static boolean isSameAsLastRun(List<Map<String, String>> currentJobs) {
        String currentHash  = hash(currentJobs);
        String previousHash = readHash();
        return currentHash.equals(previousHash);
    }

    /**
     * Compare current jobs against previous run.
     * Saves the new hash for next run.
     *
     * Returns a map with EXACTLY the keys EmailUtility and TableFormatterUtility need:
     *   totalCurrent  (int)
     *   totalPrevious (int)
     *   totalNew      (int)
     *   isNewData     (boolean)
     *   currentJobs   (List<Map<String,String>>)
     *   newJobs       (List<Map<String,String>>)
     */
    public static Map<String, Object> compareJobData(List<Map<String, String>> currentJobs) {
        List<Map<String, String>> previousJobs = readCsv();
        List<Map<String, String>> newJobs      = findNewJobs(currentJobs, previousJobs);

        boolean isNewData = !newJobs.isEmpty()
                         || currentJobs.size() != previousJobs.size();

        // Persist hash for the NEXT run to compare against
        saveHash(hash(currentJobs));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalCurrent",  currentJobs.size());
        result.put("totalPrevious", previousJobs.size());
        result.put("totalNew",      newJobs.size());
        result.put("isNewData",     isNewData);
        result.put("currentJobs",   currentJobs);
        result.put("newJobs",       newJobs);

        System.out.println("Comparison — current: " + currentJobs.size()
            + " | previous: " + previousJobs.size()
            + " | new: " + newJobs.size()
            + " | changed: " + isNewData);

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** MD5 of all jobs sorted deterministically so order doesn't matter */
    private static String hash(List<Map<String, String>> jobs) {
        try {
            StringBuilder sb = new StringBuilder();
            // Sort by link (stable unique key) so order changes don't trigger false positives
            List<Map<String, String>> sorted = new ArrayList<>(jobs);
            sorted.sort(Comparator.comparing(j -> j.getOrDefault("link", "")));
            for (Map<String, String> job : sorted) {
                sb.append(job.getOrDefault("title",   "")).append("|");
                sb.append(job.getOrDefault("company", "")).append("|");
                sb.append(job.getOrDefault("link",    "")).append("||");
            }
            MessageDigest md    = MessageDigest.getInstance("MD5");
            byte[]        bytes = md.digest(sb.toString().getBytes());
            StringBuilder hex   = new StringBuilder();
            for (byte b : bytes) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(jobs.hashCode());
        }
    }

    private static String readHash() {
        try {
            File f = new File(HASH_FILE);
            if (!f.exists()) return "";
            return new String(Files.readAllBytes(f.toPath())).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static void saveHash(String hash) {
        try (PrintWriter w = new PrintWriter(new FileWriter(HASH_FILE, false))) {
            w.print(hash);
        } catch (IOException e) {
            System.out.println("✗ Error saving hash: " + e.getMessage());
        }
    }

    /** Read the previous run's CSV so we can diff against it */
    private static List<Map<String, String>> readCsv() {
        List<Map<String, String>> jobs = new ArrayList<>();
        File f = new File(CSV_FILE);
        if (!f.exists()) return jobs;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
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
        } catch (IOException e) {
            System.out.println("✗ Error reading previous CSV: " + e.getMessage());
        }
        return jobs;
    }

    /** Jobs in current that are not in previous — matched by job link (URL) */
    private static List<Map<String, String>> findNewJobs(
            List<Map<String, String>> current,
            List<Map<String, String>> previous) {

        Set<String> prevLinks = new HashSet<>();
        for (Map<String, String> job : previous) {
            String link = job.getOrDefault("link", "").trim();
            if (!link.isEmpty()) prevLinks.add(link);
        }

        List<Map<String, String>> newJobs = new ArrayList<>();
        for (Map<String, String> job : current) {
            String link = job.getOrDefault("link", "").trim();
            if (!prevLinks.contains(link)) {
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    /** Escape double-quotes inside a CSV field value */
    private static String csv(String v) {
        if (v == null) return "";
        return v.replace("\"", "\"\"");
    }

    /** Remove surrounding quotes from a CSV field */
    private static String unquote(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\"\"", "\"");
    }

    /** Handles quoted fields that may contain commas */
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
