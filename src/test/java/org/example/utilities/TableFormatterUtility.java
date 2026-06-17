package org.example.utilities;
import java.util.*;
public class TableFormatterUtility {
    // Format job list as table
    public static String formatJobsAsTable(List<Map<String, String>> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return "No jobs available";
        }
        StringBuilder table = new StringBuilder();
        // Table header
        table.append("+-----+").append(String.join("", Collections.nCopies(50, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(30, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(20, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(40, "-"))).append("+\n");
        table.append("| No. | ").append(padRight("Job Title", 48)).append(" | ");
        table.append(padRight("Company", 28)).append(" | ");
        table.append(padRight("Posted", 18)).append(" | ");
        table.append(padRight("Apply Link", 38)).append(" |\n");
        table.append("+-----+").append(String.join("", Collections.nCopies(50, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(30, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(20, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(40, "-"))).append("+\n");
        // Table rows
        for (int i = 0; i < jobs.size(); i++) {
            Map<String, String> job = jobs.get(i);
            String title = truncate(job.getOrDefault("title", "N/A"), 48);
            String company = truncate(job.getOrDefault("company", "N/A"), 28);
            String posted = truncate(job.getOrDefault("posted", "N/A"), 18);
            String link = truncate(job.getOrDefault("link", "N/A"), 38);
            table.append("| ").append(String.format("%2d", i + 1)).append("  | ");
            table.append(padRight(title, 48)).append(" | ");
            table.append(padRight(company, 28)).append(" | ");
            table.append(padRight(posted, 18)).append(" | ");
            table.append(padRight(link, 38)).append(" |\n");
        }
        // Table footer
        table.append("+-----+").append(String.join("", Collections.nCopies(50, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(30, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(20, "-"))).append("+");
        table.append(String.join("", Collections.nCopies(40, "-"))).append("+\n");
        return table.toString();
    }
    // Format as HTML table for email
    public static String formatJobsAsHtmlTable(List<Map<String, String>> jobs, String title) {
        if (jobs == null || jobs.isEmpty()) {
            return "<p>No jobs available</p>";
        }
        StringBuilder html = new StringBuilder();
        html.append("<h3>").append(title).append("</h3>\n");
        html.append("<table border='1' cellpadding='10' cellspacing='0' style='width:100%; border-collapse:collapse;'>\n");
        html.append("<thead style='background-color:#4CAF50; color:white;'>\n");
        html.append("<tr>\n");
        html.append("<th>No.</th>\n");
        html.append("<th>Job Title</th>\n");
        html.append("<th>Company</th>\n");
        html.append("<th>Posted Date</th>\n");
        html.append("<th>Apply Link</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        for (int i = 0; i < jobs.size(); i++) {
            Map<String, String> job = jobs.get(i);
            html.append("<tr style='").append(i % 2 == 0 ? "background-color:#f9f9f9;" : "").append("'>\n");
            html.append("<td>").append(i + 1).append("</td>\n");
            html.append("<td>").append(escape(job.getOrDefault("title", "N/A"))).append("</td>\n");
            html.append("<td>").append(escape(job.getOrDefault("company", "N/A"))).append("</td>\n");
            html.append("<td>").append(escape(job.getOrDefault("posted", "N/A"))).append("</td>\n");
            html.append("<td><a href='").append(escape(job.getOrDefault("link", "#"))).append("'>View</a></td>\n");
            html.append("</tr>\n");
        }
        html.append("</tbody>\n");
        html.append("</table>\n");
        return html.toString();
    }
    // Helper: Pad string to right
    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    // Helper: Truncate string
    private static String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() > len ? s.substring(0, len - 3) + "..." : s;
    }
    // Helper: Escape HTML
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    // Get summary statistics
    public static String getSummaryStats(Map<String, Object> comparison) {
        StringBuilder stats = new StringBuilder();
        stats.append("\n========== JOB DATA SUMMARY ==========\n");
        stats.append("Total Current Jobs: ").append(comparison.get("totalCurrent")).append("\n");
        stats.append("Total Previous Jobs: ").append(comparison.get("totalPrevious")).append("\n");
        stats.append("Total New Jobs: ").append(comparison.get("totalNew")).append("\n");
        stats.append("Is New Data Available: ").append(comparison.get("isNewData")).append("\n");
        stats.append("=====================================\n");
        return stats.toString();
    }
}