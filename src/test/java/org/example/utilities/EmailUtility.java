package org.example.utilities;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
// Add these lines to the top of your file:


public class EmailUtility {
    // Gmail configuration
    private static final String SENDER_EMAIL = "lallubanala444@gmail.com";
    private static final String SENDER_PASSWORD = "btic aptc fekw gjme"; // App-specific password
//    private static final char[] PASSWORD_ARRAY = SENDER_PASSWORD.toCharArray();
    private static final String SMTP_SERVER = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    // Send email with HTML table
    public static boolean sendJobDataEmail(String recipientEmail, String subject, String htmlContent) {
        try {
            // Set up properties
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_SERVER);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
//            props.put("mail.smtp.socketFactory.port", SMTP_PORT);
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.socketFactory.fallback", "false");

//            // Get session
//            Session session = Session.getInstance(props, new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(SENDER_EMAIL, PASSWORD_ARRAY);
//                }
//            });
            // Get session
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            // Set HTML content
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            // Send
            Transport.send(message);
            System.out.println("✓ Email sent successfully to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.out.println("✗ Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // Create complete email body
//    public static String createEmailBody(Map<String, Object> comparison, String timestamp) {
//        StringBuilder html = new StringBuilder();
//        html.append("<!DOCTYPE html>\n");
//        html.append("<html>\n");
//        html.append("<head>\n");
//        html.append("<style>\n");
//        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
//        html.append("h1 { color: #333; }\n");
//        html.append("h2 { color: #4CAF50; }\n");
//        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
//        html.append("th { background-color: #4CAF50; color: white; padding: 12px; text-align: left; }\n");
//        html.append("td { padding: 12px; border-bottom: 1px solid #ddd; }\n");
//        html.append("tr:hover { background-color: #f5f5f5; }\n");
//        html.append(".summary { background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin: 20px 0; }\n");
//        html.append(".stat { margin: 10px 0; font-size: 16px; }\n");
//        html.append(".new-data { background-color: #fff3cd; padding: 10px; border-radius: 3px; }\n");
//        html.append("</style>\n");
//        html.append("</head>\n");
//        html.append("<body>\n");
//        html.append("<h1>📊 Naukri Job Search Report</h1>\n");
//        html.append("<p><strong>Generated:</strong> ").append(timestamp).append("</p>\n");
//        // Summary section
//        html.append("<div class='summary'>\n");
//        html.append("<h2>📈 Summary Statistics</h2>\n");
//        html.append("<div class='stat'>Total Current Jobs: <strong>").append(comparison.get("totalCurrent")).append("</strong></div>\n");
//        html.append("<div class='stat'>Total Previous Jobs: <strong>").append(comparison.get("totalPrevious")).append("</strong></div>\n");
//        html.append("<div class='stat'>Total New Jobs: <strong style='color: #4CAF50;'>").append(comparison.get("totalNew")).append("</strong></div>\n");
//        if ((Boolean) comparison.get("isNewData")) {
//            html.append("<div class='stat new-data'>⭐ <strong>New Data Available!</strong></div>\n");
//        } else {
//            html.append("<div class='stat'>No new jobs found since last check.</div>\n");
//        }
//        html.append("</div>\n");
//        // Current Jobs Table
//        @SuppressWarnings("unchecked")
//        List<Map<String, String>> currentJobs = (List<Map<String, String>>) comparison.get("currentJobs");
//        html.append(TableFormatterUtility.formatJobsAsHtmlTable(currentJobs, "All Current Jobs"));
//        // New Jobs Table (if available)
//        @SuppressWarnings("unchecked")
//        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
//        if (newJobs != null && !newJobs.isEmpty()) {
//            html.append(TableFormatterUtility.formatJobsAsHtmlTable(newJobs, "🆕 New Jobs Since Last Check"));
//        }
//        // Footer
//        html.append("<hr>\n");
//        html.append("<p style='color: #999; font-size: 12px;'>\n");
//        html.append("This is an automated report from the Naukri Job Search Automation Framework.<br>\n");
//        html.append("Please do not reply to this email.\n");
//        html.append("</p>\n");
//        html.append("</body>\n");
//        html.append("</html>\n");
//        return html.toString();
//    }

    public static String createEmailBody(Map<String, Object> comparison, String timestamp) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("<style>\n");
        html.append("  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; line-height: 1.6; color: #2D3748; background-color: #F7FAFC; margin: 0; padding: 0; }\n");
        html.append("  .email-container { max-width: 650px; margin: 20px auto; background: #FFFFFF; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05); border: 1px solid #E2E8F0; }\n");
        html.append("  .header { background: linear-gradient(135deg, #1A365D 0%, #2A4365 100%); padding: 30px; text-align: center; color: #FFFFFF; }\n");
        html.append("  .header h1 { margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }\n");
        html.append("  .header p { margin: 5px 0 0 0; font-size: 14px; color: #E2E8F0; opacity: 0.9; }\n");
        html.append("  .content { padding: 30px; }\n");
        html.append("  .alert-badge { background-color: #FEFCBF; border: 1px solid #F6E05E; color: #744210; padding: 12px 16px; border-radius: 6px; font-weight: 600; margin-bottom: 25px; font-size: 14px; text-align: center; }\n");
        html.append("  .summary-card { background-color: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 8px; padding: 20px; margin-bottom: 30px; }\n");
        html.append("  .summary-card h2 { margin-top: 0; margin-bottom: 15px; font-size: 16px; color: #4A5568; text-transform: uppercase; letter-spacing: 0.5px; }\n");
        html.append("  .metrics-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }\n");
        html.append("  .metric-box { background: #FFFFFF; border: 1px solid #EDF2F7; border-radius: 6px; padding: 15px; text-align: center; }\n");
        html.append("  .metric-val { display: block; font-size: 24px; font-weight: 700; color: #1A365D; line-height: 1.2; }\n");
        html.append("  .metric-val.highlight { color: #38A169; }\n");
        html.append("  .metric-lbl { display: block; font-size: 12px; color: #718096; margin-top: 4px; font-weight: 500; }\n");
        html.append("  .table-wrapper { margin: 25px 0; overflow-x: auto; }\n");
        html.append("  .footer { background-color: #F7FAFC; padding: 20px 30px; border-top: 1px solid #E2E8F0; text-align: center; font-size: 12px; color: #A0AEC0; }\n");
        html.append("  .footer a { color: #2B6CB0; text-decoration: none; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("<div class='email-container'>\n");

        // Header
        html.append("  <div class='header'>\n");
        html.append("    <h1>CareerCart Intelligence</h1>\n");
        html.append("    <p>Naukri Job Search Market Report • ").append(timestamp).append("</p>\n");
        html.append("  </div>\n");

        // Main Content
        html.append("  <div class='content'>\n");

        // Conditional Alert Banner
        if (Boolean.TRUE.equals(comparison.get("isNewData"))) {
            html.append("    <div class='alert-badge'>\n");
            html.append("      ⚡ Action Required: New relevant job openings have been detected since your last update.\n");
            html.append("    </div>\n");
        }

        // Summary Section
        html.append("    <div class='summary-card'>\n");
        html.append("      <h2>Market Overview</h2>\n");
        html.append("      <div class='metrics-grid'>\n");
        html.append("        <div class='metric-box'><span class='metric-val'>").append(comparison.get("totalCurrent")).append("</span><span class='metric-lbl'>Active Postings</span></div>\n");
        html.append("        <div class='metric-box'><span class='metric-val'>").append(comparison.get("totalPrevious")).append("</span><span class='metric-lbl'>Previous Pool</span></div>\n");
        html.append("        <div class='metric-box'><span class='metric-val highlight'>+").append(comparison.get("totalNew")).append("</span><span class='metric-lbl'>New Discoveries</span></div>\n");
        html.append("      </div>\n");
        html.append("    </div>\n");

        // New Jobs Table
        @SuppressWarnings("unchecked")
        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
        if (newJobs != null && !newJobs.isEmpty()) {
            html.append("    <div class='table-wrapper'>\n");
            html.append(TableFormatterUtility.formatJobsAsHtmlTable(newJobs, "Newly Identified Positions"));
            html.append("    </div>\n");
        }

        // Current Jobs Table
        @SuppressWarnings("unchecked")
        List<Map<String, String>> currentJobs = (List<Map<String, String>>) comparison.get("currentJobs");
        if (currentJobs != null && !currentJobs.isEmpty()) {
            html.append("    <div class='table-wrapper'>\n");
            html.append(TableFormatterUtility.formatJobsAsHtmlTable(currentJobs, "Full Active Inventory"));
            html.append("    </div>\n");
        }

        html.append("  </div>\n"); // End Content

        // Footer
        html.append("  <div class='footer'>\n");
        html.append("    <p>This automated analysis was generated by the <strong>CareerCart</strong> Analytics Framework engine.</p>\n");
        html.append("    <p>Please do not reply directly to this systemic transmission.</p>\n");
        html.append("  </div>\n");

        html.append("</div>\n"); // End Container

        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

}