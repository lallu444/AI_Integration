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
    public static String createEmailBody(Map<String, Object> comparison, String timestamp) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("h1 { color: #333; }\n");
        html.append("h2 { color: #4CAF50; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        html.append("th { background-color: #4CAF50; color: white; padding: 12px; text-align: left; }\n");
        html.append("td { padding: 12px; border-bottom: 1px solid #ddd; }\n");
        html.append("tr:hover { background-color: #f5f5f5; }\n");
        html.append(".summary { background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin: 20px 0; }\n");
        html.append(".stat { margin: 10px 0; font-size: 16px; }\n");
        html.append(".new-data { background-color: #fff3cd; padding: 10px; border-radius: 3px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1>📊 Naukri Job Search Report</h1>\n");
        html.append("<p><strong>Generated:</strong> ").append(timestamp).append("</p>\n");
        // Summary section
        html.append("<div class='summary'>\n");
        html.append("<h2>📈 Summary Statistics</h2>\n");
        html.append("<div class='stat'>Total Current Jobs: <strong>").append(comparison.get("totalCurrent")).append("</strong></div>\n");
        html.append("<div class='stat'>Total Previous Jobs: <strong>").append(comparison.get("totalPrevious")).append("</strong></div>\n");
        html.append("<div class='stat'>Total New Jobs: <strong style='color: #4CAF50;'>").append(comparison.get("totalNew")).append("</strong></div>\n");
        if ((Boolean) comparison.get("isNewData")) {
            html.append("<div class='stat new-data'>⭐ <strong>New Data Available!</strong></div>\n");
        } else {
            html.append("<div class='stat'>No new jobs found since last check.</div>\n");
        }
        html.append("</div>\n");
        // Current Jobs Table
        @SuppressWarnings("unchecked")
        List<Map<String, String>> currentJobs = (List<Map<String, String>>) comparison.get("currentJobs");
        html.append(TableFormatterUtility.formatJobsAsHtmlTable(currentJobs, "All Current Jobs"));
        // New Jobs Table (if available)
        @SuppressWarnings("unchecked")
        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
        if (newJobs != null && !newJobs.isEmpty()) {
            html.append(TableFormatterUtility.formatJobsAsHtmlTable(newJobs, "🆕 New Jobs Since Last Check"));
        }
        // Footer
        html.append("<hr>\n");
        html.append("<p style='color: #999; font-size: 12px;'>\n");
        html.append("This is an automated report from the Naukri Job Search Automation Framework.<br>\n");
        html.append("Please do not reply to this email.\n");
        html.append("</p>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }
}