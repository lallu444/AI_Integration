package org.example.runners;

import org.example.drivers.DriverManager;
import org.example.model.JobSearchGroup;
import org.example.model.Member;
import org.example.pages.NaukriJobSearchPage;
import org.example.utilities.CsvMemberReader;
import org.example.utilities.MemberGroupingService;
import org.example.utilities.EmailUtility;

import java.util.List;

public class BatchJobSearchRunner {

    public static void main(String[] args) throws Exception {
        String csvPath = System.getProperty("csv.path", "members.csv");

        List<Member> members = new CsvMemberReader().readMembers(csvPath);
        List<JobSearchGroup> groups = new MemberGroupingService().groupMembers(members);

        System.out.println("Total members: " + members.size() + " | Total groups formed: " + groups.size());

        EmailUtility emailUtility = new EmailUtility();

        for (JobSearchGroup group : groups) {
            try {
                System.out.println("Processing group: " + group.getRole() + " | " + group.getLocation()
                        + " | " + group.getExperience() + " | members: " + group.getMemberEmails());

                DriverManager.initializeDriver("chrome");
                NaukriJobSearchPage page = new NaukriJobSearchPage(DriverManager.getDriver());

                page.searchJobs(group.getRole(), group.getLocation(), group.getExperience());
                var jobs = page.getTopJobs(); // however your page object currently returns the job list

                emailUtility.sendMail(group.getMemberEmails(), group.getRole(), group.getLocation(), jobs);

                System.out.println("Mail sent successfully for group: " + group.getRole());
            } catch (Exception e) {
                System.err.println("Group failed: " + group.getRole() + " - " + e.getMessage());
                // continue to next group, don't abort whole batch
            } finally {
                DriverManager.quitDriver();
                Thread.sleep(3000); // small backoff between groups to avoid Naukri throttling
            }
        }
    }
}