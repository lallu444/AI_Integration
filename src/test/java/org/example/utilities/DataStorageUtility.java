package org.example.utilities;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

public class DataStorageUtility {

    // Single CSV file — always same name, always overwritten
    private static final String CSV_FILE = "jobs_output.csv";
    private static final String HASH_FILE = "jobs_hash.txt";

    // Save job data — OVERWRITES previous CSV every time
    public static void saveJobData(List<Map<String, String>> jobs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, false))) { // false = overwrite
            // Write header
            writer.println("Title,Company,Posted,Link");

            // Write each job
            for (Map<String, String> job : jobs) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCsv(job.getOrDefault("title", "")),
                        escapeCsv(job.getOrDefault("company", "")),
                        escapeCsv(job.getOrDefault("posted", "")),
                        escapeCsv(job.getOrDefault("link", ""))
                );
            }
            System.out.println("✓ CSV saved/overwritten: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Error saving CSV: " + e.getMessage());
        }
    }

    // Compare current jobs with previous run using hash
    public static Map<String, Object> compareJobData(List<Map<String, String>> currentJobs) {
        Map<String, Object> result = new HashMap<>();

        String currentHash = generateHash(currentJobs);
        String previousHash = readPreviousHash();

        boolean isSameData = currentHash.equals(previousHash);
        boolean isNewData  = !isSameData;

        // Find truly new jobs (not in previous CSV)
        List<Map<String, String>> previousJobs = readPreviousCsvJobs();
        List<Map<String, String>> newJobs = findNewJobs(currentJobs, previousJobs);

        // Save current hash for next run comparison
        savePreviousHash(currentHash);

        result.put("isNewData",     isNewData);
        result.put("isSameData",    isSameData);
        result.put("newJobs",       newJobs);
        result.put("currentJobs",   currentJobs);
        result.put("previousJobs",  previousJobs);
        result.put("newJobsCount",  newJobs.size());
        result.put("totalCount",    currentJobs.size());

        System.out.println("Data comparison — Same as last run: " + isSameData +
                " | New jobs found: " + newJobs.size());
        return result;
    }

    // Check if current data is identical to previous run
    public static boolean isSameAsLastRun(List<Map<String, String>> currentJobs) {
        String currentHash  = generateHash(currentJobs);
        String previousHash = readPreviousHash();
        return currentHash.equals(previousHash);
    }

    // ─── Private Helpers ────────────────────────────────────────────────────

    // Generate a stable hash from job list for comparison
    private static String generateHash(List<Map<String, String>> jobs) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map<String, String> job : jobs) {
                // Sort keys so order doesn't affect hash
                TreeMap<String, String> sorted = new TreeMap<>(job);
                sb.append(sorted.toString());
            }
            MessageDigest md  = MessageDigest.getInstance("MD5");
            byte[] hashBytes  = md.digest(sb.toString().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(jobs.hashCode());
        }
    }

    // Read hash saved from previous run
    private static String readPreviousHash() {
        try {
            File f = new File(HASH_FILE);
            if (!f.exists()) return "";
            return new String(Files.readAllBytes(f.toPath())).trim();
        } catch (IOException e) {
            return "";
        }
    }

    // Save current hash for next run to compare against
    private static void savePreviousHash(String hash) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HASH_FILE, false))) {
            writer.print(hash);
        } catch (IOException e) {
            System.out.println("Error saving hash: " + e.getMessage());
        }
    }

    // Read jobs from the existing CSV (previous run's data)
    private static List<Map<String, String>> readPreviousCsvJobs() {
        List<Map<String, String>> jobs = new ArrayList<>();
        File f = new File(CSV_FILE);
        if (!f.exists()) return jobs;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = parseCsvLine(line);
                if (parts.length >= 4) {
                    Map<String, String> job = new HashMap<>();
                    job.put("title",   parts[0].replace("\"", "").trim());
                    job.put("company", parts[1].replace("\"", "").trim());
                    job.put("posted",  parts[2].replace("\"", "").trim());
                    job.put("link",    parts[3].replace("\"", "").trim());
                    jobs.add(job);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading previous CSV: " + e.getMessage());
        }
        return jobs;
    }

    // Find jobs in current list that weren't in previous list (by link)
    private static List<Map<String, String>> findNewJobs(
            List<Map<String, String>> current,
            List<Map<String, String>> previous) {

        Set<String> previousLinks = new HashSet<>();
        for (Map<String, String> job : previous) {
            String link = job.getOrDefault("link", "");
            if (!link.isEmpty()) previousLinks.add(link);
        }
        List<Map<String, String>> newJobs = new ArrayList<>();
        for (Map<String, String> job : current) {
            String link = job.getOrDefault("link", "");
            if (!previousLinks.contains(link)) {
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    // Escape special characters for CSV
    private static String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }

    // Simple CSV line parser (handles quoted fields)
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}