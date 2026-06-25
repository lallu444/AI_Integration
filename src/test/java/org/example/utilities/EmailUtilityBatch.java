package org.example.utilities;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class EmailUtilityBatch {

    // Gmail / sender config ─────────────────────────────────────────────────
    private static final String SENDER_EMAIL    = "careercartindia@gmail.com";
    private static final String SENDER_PASSWORD = "pgox kbaq xldq zdeg"; // App password
    private static final String SMTP_SERVER     = "smtp.gmail.com";
    private static final int    SMTP_PORT       = 587;

    // ────────────────────────────────────────────────────────────────────────
    // EXISTING METHOD — single recipient TO
    // ────────────────────────────────────────────────────────────────────────
    public static boolean sendJobDataEmail(String recipientEmail,
                                           String subject,
                                           String htmlContent) {
        try {
            Session session = buildSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            setHtmlBody(message, htmlContent);
            Transport.send(message);
            System.out.println("\n✅ Email sent to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.out.println("\n❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // NEW METHOD — send FROM a Google Group TO the same Google Group
    //              (the group distributes to all its members automatically)
    //
    // fromGroupEmail  : "careercart-qa-3-5@googlegroups.com"
    // toGroupEmail    : "careercart-qa-3-5@googlegroups.com"
    //                   (same address; the Google Group fan-outs to members)
    // batchLabel      : e.g. "Batch 1: QA Analyst_3_Hyderabad" — shown in console
    // ────────────────────────────────────────────────────────────────────────
    public static boolean sendJobDataEmailViaGroup(String fromGroupEmail,
                                                   String toGroupEmail,
                                                   String subject,
                                                   String htmlContent,
                                                   String batchLabel) {
        try {
            Session session = buildSession();
            Message message = new MimeMessage(session);

            // Sender is our authenticated Gmail account but "on behalf of" the group.
            // NOTE: Google Groups require the sender to be a member of the group
            //       (or the group must allow external senders).
            message.setFrom(new InternetAddress(SENDER_EMAIL));

            // Reply-To set to the group so replies go to the group thread
            message.setReplyTo(new Address[]{new InternetAddress(toGroupEmail)});

            // TO = the Google Group address (which fans out to all members)
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toGroupEmail));

            message.setSubject(subject);
            setHtmlBody(message, htmlContent);
            Transport.send(message);
            System.out.println("\n✅ Email sent to group " + toGroupEmail
                    + "  [" + batchLabel + "]");
            return true;
        } catch (MessagingException e) {
            System.out.println("\n❌ Error sending group email for ["
                    + batchLabel + "]: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // CONVENIENCE — send to ALL batches (one email per group)
    // ────────────────────────────────────────────────────────────────────────
    public static void sendEmailToAllBatches(Map<String, MembersBatchLoader.Batch> batches,
                                             String subject,
                                             String htmlContent) {
        if (batches == null || batches.isEmpty()) {
            System.out.println("[EmailUtility] No batches to send to.");
            return;
        }
        int batchNum = 1;
        for (MembersBatchLoader.Batch batch : batches.values()) {
            String label = "Batch " + batchNum + ": " + batch.batchKey;
            System.out.println("\n📧 Sending to " + label + " → " + batch.groupEmail);
            sendJobDataEmailViaGroup(
                    SENDER_EMAIL,
                    batch.groupEmail,
                    subject,
                    htmlContent,
                    label
            );
            batchNum++;
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // HTML email body builder (unchanged from original)
    // ────────────────────────────────────────────────────────────────────────
    public static String createEmailBody(Map<String, Object> comparison, String timestamp) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("<style>\n");
        html.append("  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; line-height: 1.6; color: #2D3748; background-color: #F7FAFC; margin: 0; padding: 0; }\n");
        html.append("  .email-container { max-width: 650px; margin: 20px auto; background: #FFFFFF; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border: 1px solid #E2E8F0; }\n");
        html.append("  .header { background: linear-gradient(135deg, #1A365D 0%, #2A4365 100%); padding: 30px; text-align: center; color: #FFFFFF; }\n");
        html.append("  .header h1 { margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }\n");
        html.append("  .header p  { margin: 5px 0 0 0; font-size: 14px; color: #E2E8F0; opacity: 0.9; }\n");
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
        html.append("</style>\n</head>\n<body>\n");
        html.append("<div class='email-container'>\n");

        // Header
        html.append("  <div class='header'>\n");
        html.append("    <h1>CareerCart Intelligence</h1>\n");
        html.append("    <p>Naukri Job Search Market Report &mdash; ").append(timestamp).append("</p>\n");
        html.append("  </div>\n");

        // Content
        html.append("  <div class='content'>\n");
        if (Boolean.TRUE.equals(comparison.get("isNewData"))) {
            html.append("    <div class='alert-badge'>&#9888; Action Required: New relevant job openings detected since your last update.</div>\n");
        }

        // Summary metrics
        html.append("    <div class='summary-card'>\n");
        html.append("      <h2>Market Overview</h2>\n");
        html.append("      <div class='metrics-grid'>\n");
        html.append("        <div class='metric-box'><span class='metric-val'>").append(comparison.get("totalCurrent")).append("</span><span class='metric-lbl'>Active Postings</span></div>\n");
        html.append("        <div class='metric-box'><span class='metric-val'>").append(comparison.get("totalPrevious")).append("</span><span class='metric-lbl'>Previous Pool</span></div>\n");
        html.append("        <div class='metric-box'><span class='metric-val highlight'>+").append(comparison.get("totalNew")).append("</span><span class='metric-lbl'>New Discoveries</span></div>\n");
        html.append("      </div>\n    </div>\n");

        // New jobs table
        @SuppressWarnings("unchecked")
        List<Map<String, String>> newJobs = (List<Map<String, String>>) comparison.get("newJobs");
        if (newJobs != null && !newJobs.isEmpty()) {
            html.append("    <div class='table-wrapper'>\n");
            html.append(TableFormatterUtility.formatJobsAsHtmlTable(newJobs, "Newly Identified Positions"));
            html.append("    </div>\n");
        }

        // All current jobs table
        @SuppressWarnings("unchecked")
        List<Map<String, String>> currentJobs = (List<Map<String, String>>) comparison.get("currentJobs");
        if (currentJobs != null && !currentJobs.isEmpty()) {
            html.append("    <div class='table-wrapper'>\n");
            html.append(TableFormatterUtility.formatJobsAsHtmlTable(currentJobs, "Full Active Inventory"));
            html.append("    </div>\n");
        }

        html.append("  </div>\n"); // end content

        // Footer
        html.append("  <div class='footer'>\n");
        html.append("    <p>This automated analysis was generated by the <strong>CareerCart</strong> Analytics Framework.</p>\n");
        html.append("    <p>Please do not reply directly to this transmission.</p>\n");
        html.append("  </div>\n");
        html.append("</div>\n</body>\n</html>\n");
        return html.toString();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────────────
    private static Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host",              SMTP_SERVER);
        props.put("mail.smtp.port",              SMTP_PORT);
        props.put("mail.smtp.auth",              "true");
        props.put("mail.smtp.starttls.enable",   "true");
        props.put("mail.smtp.starttls.required", "true");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }

    private static void setHtmlBody(Message message, String htmlContent) throws MessagingException {
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);
        message.setContent(multipart);
    }
}
