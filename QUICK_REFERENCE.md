# QUICK REFERENCE GUIDE
## 🎯 WHAT WAS IMPLEMENTED
✓ Table Format Output - ASCII and HTML tables
✓ Data Storage - CSV files with timestamps  
✓ Data Comparison - New vs Previous detection
✓ Final Data Selection - Smart logic for what to return
✓ Email Integration - Send tables to lallubanala444@gmail.com
## 📁 NEW FILES
1. src/main/java/org/example/utilities/DataStorageUtility.java
   - Manages CSV file storage and retrieval
   - Compares job data to find new entries
   - Methods: saveJobData(), loadJobData(), compareJobData()
2. src/main/java/org/example/utilities/TableFormatterUtility.java
   - Formats data as ASCII and HTML tables
   - Generates summary statistics
   - Methods: formatJobsAsTable(), formatJobsAsHtmlTable()
3. src/main/java/org/example/utilities/EmailUtility.java
   - Sends emails via Gmail SMTP
   - Creates professional HTML email body
   - Methods: sendJobDataEmail(), createEmailBody()
## 🔄 UPDATED FILES
- pom.xml (Added javax.mail dependencies)
- NaukriJobSearchPage.java (Added new methods for complete workflow)
## 🚀 HOW TO USE
### Option 1: Execute Complete Workflow (Recommended)
\\\java
page.executeCompleteWorkflow(
    "QA Engineer",
    "5 years",
    "Hyderabad",
    "lallubanala444@gmail.com"
);
\\\
This does EVERYTHING:
1. Navigate to Naukri
2. Search and filter jobs
3. Retrieve top 10 jobs
4. Save to CSV file
5. Compare with previous data
6. Format as table
7. Send email
8. Print to console
### Option 2: Step-by-Step Approach
\\\java
// Search and filter
page.navigateToNaukri();
page.searchSkills("QA Engineer");
page.filterJobListings();
// Get and process data
List<Map<String, String>> finalData = page.getAndProcessJobData();
// Display in table
page.printJobsInTableFormat(finalData);
// Send email
page.sendFinalDataViaEmail("lallubanala444@gmail.com");
\\\
## 📊 DATA FLOW
1. Retrieve jobs from Naukri
   ↓
2. Save to: src/test/resources/job_data/jobs_YYYYMMDD_HHMMSS.csv
   ↓
3. Compare with previous CSV file
   ↓
4. Find new jobs (by comparing links)
   ↓
5. Logic: New jobs found? → YES: Use only new | NO: Use all
   ↓
6. Format final data as table
   ↓
7. Send table via email to lallubanala444@gmail.com
   ↓
8. Print summary to console
## 📧 EMAIL FEATURES
- HTML formatted with professional styling
- Summary statistics section
- All current jobs table
- New jobs table (if available)
- Color-coded headers and alternating rows
- Responsive design
## 📈 COMPARISON LOGIC
`
Current Jobs Count: 10
Previous Jobs Count: 8
New Jobs Count: 2
Is New Data Available: YES
Final Data = 2 new jobs (only new jobs are returned)
`
If no new jobs:
`
Current Jobs Count: 10
Previous Jobs Count: 10
New Jobs Count: 0
Is New Data Available: NO
Final Data = All 10 current jobs
`
## 💾 FILE STORAGE
Location: src/test/resources/job_data/
Files are named: jobs_20260616_143022.csv
CSV Format:
`
Job Title,Company,Posted Date,Apply Link
QA Engineer,Tech Corp,2 hours ago,https://naukri.com/job/...
Senior QA,ABC Corp,4 hours ago,https://naukri.com/job/...
`
## ✉️ EMAIL CONFIG
- From: noreply.automation.bot@gmail.com
- To: lallubanala444@gmail.com
- SMTP: smtp.gmail.com:587
- Authentication: App-specific password (configured)
## 🎨 TABLE FORMAT
Console Output (ASCII):
\\\
+-----+--------------------------------------------------+--------+--------+
| No. | Job Title                                        | Company| Posted |
+-----+--------------------------------------------------+--------+--------+
|  1  | QA Engineer - Automation                         | TechCo | 2h ago |
+-----+--------------------------------------------------+--------+--------+
\\\
Email Output (HTML):
Beautiful formatted table with:
- Green header with white text
- Alternating row colors
- Clickable links
- Professional styling
## 🔧 KEY METHODS
NaukriJobSearchPage:
- executeCompleteWorkflow() - One method to rule them all
- getAndProcessJobData() - Gets and processes with comparison
- printJobsInTableFormat() - Prints ASCII table
- sendFinalDataViaEmail() - Sends HTML email
DataStorageUtility:
- saveJobData() - Save jobs to CSV
- compareJobData() - Compare and detect new jobs
- loadJobData() - Load from CSV file
TableFormatterUtility:
- formatJobsAsTable() - ASCII formatting
- formatJobsAsHtmlTable() - HTML formatting
- getSummaryStats() - Statistics summary
EmailUtility:
- sendJobDataEmail() - Send via Gmail
- createEmailBody() - Generate HTML body
## ✅ COMPILATION
Build Status: SUCCESS
All classes: Compiled
JAR generated: target/AI_Integration-1.0-SNAPSHOT.jar
Ready to deploy: YES
## 🎬 NEXT STEPS
1. Run the test: mvn test
2. Watch the automation in action
3. Check CSV file: src/test/resources/job_data/
4. Check email inbox: lallubanala444@gmail.com
5. Review console output
═════════════════════════════════════════════════════════════════════════════
ALL REQUIREMENTS COMPLETED ✓
