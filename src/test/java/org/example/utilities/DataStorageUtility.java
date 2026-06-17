package org.example.utilities;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class DataStorageUtility {
    private static final String DATA_DIR = "src/test/resources/job_data";
    private static final String DATA_FILE_PREFIX = "jobs_";
    private static final String DATA_FILE_SUFFIX = ".csv";
    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.out.println("Error creating data directory: " + e.getMessage());
        }
    }
    // Save job listings to file
    public static String saveJobData(List<Map<String, String>> jobsList) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = DATA_FILE_PREFIX + timestamp + DATA_FILE_SUFFIX;
            Path filepath = Paths.get(DATA_DIR, filename);
            StringBuilder csv = new StringBuilder();
            csv.append("Job Title,Company,Posted Date,Apply Link\n");
            for (Map<String, String> job : jobsList) {
                csv.append(job.getOrDefault("title", "")).append(",");
                csv.append(job.getOrDefault("company", "")).append(",");
                csv.append(job.getOrDefault("posted", "")).append(",");
                csv.append(job.getOrDefault("link", "")).append("\n");
            }
            Files.write(filepath, csv.toString().getBytes());
            System.out.println("Job data saved to: " + filepath);
            return filepath.toString();
        } catch (IOException e) {
            System.out.println("Error saving job data: " + e.getMessage());
            return null;
        }
    }
    // Get latest job data file
    public static String getLatestJobDataFile() {
        try {
            File dir = new File(DATA_DIR);
            File[] files = dir.listFiles((d, name) -> name.startsWith(DATA_FILE_PREFIX) && name.endsWith(DATA_FILE_SUFFIX));
            if (files == null || files.length == 0) {
                return null;
            }
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            return files[0].getAbsolutePath();
        } catch (Exception e) {
            System.out.println("Error getting latest job data file: " + e.getMessage());
            return null;
        }
    }
    // Load job data from file
    public static List<Map<String, String>> loadJobData(String filepath) {
        List<Map<String, String>> jobs = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filepath));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                Map<String, String> job = new HashMap<>();
                job.put("title", parts.length > 0 ? parts[0] : "");
                job.put("company", parts.length > 1 ? parts[1] : "");
                job.put("posted", parts.length > 2 ? parts[2] : "");
                job.put("link", parts.length > 3 ? parts[3] : "");
                jobs.add(job);
            }
        } catch (IOException e) {
            System.out.println("Error loading job data: " + e.getMessage());
        }
        return jobs;
    }
    // Compare current data with previous data
    public static Map<String, Object> compareJobData(List<Map<String, String>> currentJobs) {
        Map<String, Object> result = new HashMap<>();
        String previousFile = getLatestJobDataFile();
        List<Map<String, String>> previousJobs = new ArrayList<>();
        if (previousFile != null) {
            previousJobs = loadJobData(previousFile);
        }
        List<Map<String, String>> newJobs = findNewJobs(currentJobs, previousJobs);
        result.put("currentJobs", currentJobs);
        result.put("previousJobs", previousJobs);
        result.put("newJobs", newJobs);
        result.put("isNewData", newJobs.size() > 0);
        result.put("totalCurrent", currentJobs.size());
        result.put("totalPrevious", previousJobs.size());
        result.put("totalNew", newJobs.size());
        return result;
    }
    // Find new jobs by comparing links
    private static List<Map<String, String>> findNewJobs(List<Map<String, String>> current, List<Map<String, String>> previous) {
        List<Map<String, String>> newJobs = new ArrayList<>();
        Set<String> previousLinks = new HashSet<>();
        for (Map<String, String> job : previous) {
            previousLinks.add(job.get("link"));
        }
        for (Map<String, String> job : current) {
            if (!previousLinks.contains(job.get("link"))) {
                newJobs.add(job);
            }
        }
        return newJobs;
    }
}