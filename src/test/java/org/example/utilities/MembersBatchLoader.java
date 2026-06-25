package org.example.utilities;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Reads Members.csv and groups members into batches.
 *
 * Grouping key: Role + "_" + Experience + "_" + Location  (case-insensitive)
 * Example batch key: "QA Analyst_5_Hyderabad"
 *
 * Google Group mapping (add more entries here as needed):
 *   3-5 yrs QA / Hyderabad → careercart-qa-3-5@googlegroups.com
 *
 * Usage:
 *   Map<String, MembersBatchLoader.Batch> batches = MembersBatchLoader.loadBatches("Members.csv");
 *   for (MembersBatchLoader.Batch batch : batches.values()) {
 *       System.out.println(batch.groupEmail + " -> " + batch.memberEmails);
 *   }
 */
public class MembersBatchLoader {

    // -----------------------------------------------------------------------
    // Google Group assignment rules
    // Extend this map to add new groups for other roles / locations / years.
    // -----------------------------------------------------------------------
    private static final Map<String, String> GROUP_RULES = new LinkedHashMap<>();

    static {
        /*// Key format: ROLE (lower-trimmed) | EXP_BUCKET | LOCATION (lower-trimmed)
        // Exp bucket: "3-5" means experience 3,4,5 years
        GROUP_RULES.put("qa analyst|1|hyderabad",  "careercart-qa-1@googlegroups.com");
        GROUP_RULES.put("qa engineer|2|hyderabad", "careercart-qa-3-5@googlegroups.com");
        // Add more rules here, e.g.:
        GROUP_RULES.put("mis analyst|1-3|hyderabad", "careercart-mis-1-3@googlegroups.com");*/
        for (int exp = 1; exp <= 10; exp++) {
            String expBucket;
            // Determine the experience bucket string based on the year
            if (exp == 1) {
                expBucket = "1";
            } else if (exp == 2) {
                expBucket = "2";
            } else if (exp >= 3 && exp <= 5) {
                expBucket = "3-5";
            } else if (exp >= 6 && exp <= 8) {
                expBucket = "6-8";
            } else { // 9 and 10 years
                expBucket = "9-10";
            }

            // Generate keys and values for both roles dynamically
            String analystKey = String.format("qa analyst|%s|hyderabad", expBucket);
            String misKey = String.format("mis analyst|%s|hyderabad", expBucket);
            String email = String.format("careercart-qa-%s@googlegroups.com", expBucket);

            // Put them into your rules map
            GROUP_RULES.put(analystKey, email);
            GROUP_RULES.put(misKey, email);
        }

    }

    // -----------------------------------------------------------------------
    // Public model
    // -----------------------------------------------------------------------
    public static class Member {
        public final String name;
        public final String email;
        public final int    experience;
        public final String role;
        public final String location;

        public Member(String name, String email, int experience, String role, String location) {
            this.name       = name.trim();
            this.email      = email.trim();
            this.experience = experience;
            this.role       = role.trim();
            this.location   = location.trim();
        }

        @Override
        public String toString() {
            return name + " <" + email + "> (" + role + ", " + experience + "y, " + location + ")";
        }
    }

    public static class Batch {
        public final String       batchKey;    // e.g. "QA Analyst_4_Hyderabad"
        public final String       groupEmail;  // Google Group address
        public final List<Member> members;
        public final List<String> memberEmails;

        public Batch(String batchKey, String groupEmail, List<Member> members) {
            this.batchKey     = batchKey;
            this.groupEmail   = groupEmail;
            this.members      = Collections.unmodifiableList(members);
            List<String> emails = new ArrayList<>();
            for (Member m : members) emails.add(m.email);
            this.memberEmails = Collections.unmodifiableList(emails);
        }

        @Override
        public String toString() {
            return "[" + batchKey + "] group=" + groupEmail + " members=" + memberEmails;
        }
    }

    // -----------------------------------------------------------------------
    // Core loader
    // -----------------------------------------------------------------------

    /**
     * Load batches from the Members.csv file.
     * Members with no matching group rule are silently skipped (logged to console).
     *
     * @param csvPath path to Members.csv (relative or absolute)
     * @return ordered map: batchKey → Batch (one Batch per unique Role+Exp+Location combo)
     */
    public static Map<String, Batch> loadBatches(String csvPath) {
        List<Member> members = readCsv(csvPath);

        // Group members by (role, experience, location) key
        // Map: batchKey → list of members
        Map<String, List<Member>> grouped = new LinkedHashMap<>();
        for (Member m : members) {
            String key = buildBatchKey(m);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
        }

        // Resolve Google Group for each batch key, skip those without a group
        Map<String, Batch> batches = new LinkedHashMap<>();
        for (Map.Entry<String, List<Member>> entry : grouped.entrySet()) {
            String batchKey  = entry.getKey();
            String groupEmail = resolveGroup(entry.getValue().get(0));
            if (groupEmail == null) {
                System.out.println("[MembersBatchLoader] No group rule for batch: " + batchKey + " — skipping.");
                continue;
            }
            batches.put(batchKey, new Batch(batchKey, groupEmail, entry.getValue()));
            System.out.println("[MembersBatchLoader] Batch '" + batchKey + "' → " + groupEmail
                    + "  members: " + entry.getValue().size());
        }
        return batches;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Human-readable batch key, e.g. "QA Analyst_5_Hyderabad" */
    private static String buildBatchKey(Member m) {
        return m.role + "_" + m.experience + "_" + m.location;
    }

    /** Look up the Google Group for a member using GROUP_RULES */
    private static String resolveGroup(Member m) {
        String role     = m.role.toLowerCase().trim();
        String location = m.location.toLowerCase().trim();
        int    exp      = m.experience;

        for (Map.Entry<String, String> rule : GROUP_RULES.entrySet()) {
            String[] parts = rule.getKey().split("\\|");
            if (parts.length != 3) continue;

            String rRole    = parts[0].trim();
            String rBucket  = parts[1].trim();   // e.g. "3-5"
            String rLoc     = parts[2].trim();

            if (!role.equalsIgnoreCase(rRole))     continue;
            if (!location.equalsIgnoreCase(rLoc))  continue;
            if (!matchesBucket(exp, rBucket))      continue;

            return rule.getValue();
        }
        return null;
    }

    /** Check if experience (int years) falls within bucket like "3-5" */
    private static boolean matchesBucket(int exp, String bucket) {
        if (bucket.contains("-")) {
            String[] parts = bucket.split("-");
            int lo = Integer.parseInt(parts[0].trim());
            int hi = Integer.parseInt(parts[1].trim());
            return exp >= lo && exp <= hi;
        }
        // Exact match
        return exp == Integer.parseInt(bucket.trim());
    }

    /** Parse Members.csv → list of valid Member objects */
    private static List<Member> readCsv(String csvPath) {
        List<Member> members = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvPath), Charset.forName("ISO-8859-1")))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }  // skip header
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] cols = line.split(",", -1);
                if (cols.length < 5) continue;

                String name     = cols[0].trim();
                String email    = cols[1].trim();
                String expStr   = cols[2].trim();
                String role     = cols[3].trim();
                String location = cols[4].trim();

                // Skip rows that look empty or have no email
                if (name.isEmpty() || email.isEmpty() || !email.contains("@")) continue;

                int exp = 0;
                try { exp = Integer.parseInt(expStr); } catch (NumberFormatException ignored) { continue; }

                members.add(new Member(name, email, exp, role, location));
            }
        } catch (IOException e) {
            System.out.println("[MembersBatchLoader] Error reading " + csvPath + ": " + e.getMessage());
        }
        System.out.println("[MembersBatchLoader] Loaded " + members.size() + " valid members from " + csvPath);
        return members;
    }

    /**
     * Helper: add emails to GROUP_RULES dynamically at runtime (for future batches).
     * Call this before loadBatches() to register additional Google Groups.
     *
     * @param rolePattern     role (lower-case), e.g. "mis analyst"
     * @param expBucket       e.g. "1-3" or "5"
     * @param locationPattern location (lower-case), e.g. "bangalore"
     * @param googleGroupEmail target group email
     */
    public static void addGroupRule(String rolePattern, String expBucket,
                                    String locationPattern, String googleGroupEmail) {
        String key = rolePattern.toLowerCase().trim() + "|"
                   + expBucket.trim() + "|"
                   + locationPattern.toLowerCase().trim();
        GROUP_RULES.put(key, googleGroupEmail);
        System.out.println("[MembersBatchLoader] Added rule: " + key + " → " + googleGroupEmail);
    }
}
