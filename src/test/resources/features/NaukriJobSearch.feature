Feature: Naukri Job Search

  @Test
  Scenario: Search for QA Engineer jobs on Naukri
    Given User navigates to Naukri website
    When User searches for skill "QA Engineer"
    And User selects experience "5 years"
    And User enters location "Hyderabad"
    And User clicks search button
    Then User retrieves top 10 job apply links and prints to console

  @MailReport
  Scenario: send Mail report of the latest jobs
    Given navigate to naukri and search for skill "QA Automation", experience "5 years", location "Hyderabad"
#      Then retrieve top 10 job apply links and print to console

  @MailReportPropertyReader
  Scenario: send Mail report of the latest jobs
    Given navigate to naukri and search jobs

  @MailReportPropertyReaderAPI
  Scenario: send Mail report of the latest jobs
    Given navigate to naukri and search jobs from API

  @PropertyReader
  Scenario: Read properties from config file
    Given read "Platform" from config file


  @updateNaukri
  Scenario Outline: Update Naukri everyday
    Given User navigates to Naukri website
    And login to site using '<mail>' and '<password>'
    Then update the bio
    Examples:
      | mail                     | password   |
      | lallubanala444@gmail.com | Lallu@444  |
      | keerthanak1710@gmail.com | Kittu1717! |
