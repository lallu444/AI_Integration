package org.example.stepDefinitions;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.example.drivers.DriverManager;
import org.example.pages.NaukriJobSearchPage;
import org.example.pages.NaukriJobSearchPageAPI;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.List;
import org.example.utilities.Property;

public class NaukriJobSearchSteps {
    private WebDriver driver;
    private NaukriJobSearchPage naukriPage;
    private NaukriJobSearchPageAPI naukriApiPage;

    public NaukriJobSearchSteps() {
        this.driver = DriverManager.getDriver();
        Property property = new Property();
        this.naukriPage = new NaukriJobSearchPage(driver);
        this.naukriApiPage = new NaukriJobSearchPageAPI(driver);
    }
    @Given("User navigates to Naukri website")
    public void userNavigatesToNaukriWebsite() {
        driver = DriverManager.getDriver();
        naukriPage = new NaukriJobSearchPage(driver);
        naukriPage.navigateToNaukri();
        System.out.println("User navigated to Naukri website");
    }
    @When("User searches for skill {string}")
    public void userSearchesForSkill(String skill) throws IOException {
        naukriPage.searchSkills(skill);
        System.out.println("User entered skill: " + skill);
    }
    @When("User selects experience {string}")
    public void userSelectsExperience(String experience) {
        naukriPage.selectExperience(experience);
        System.out.println("User selected experience: " + experience);
    }
    @When("User enters location {string}")
    public void userEntersLocation(String location) throws IOException {
        naukriPage.enterLocation(location);
        System.out.println("User entered location: " + location);
    }
    @When("User clicks search button")
    public void userClicksSearchButton() {
        naukriPage.clickSearchButton();
        System.out.println("User clicked search button");
    }
    @Then("User retrieves top {int} job apply links and prints to console")
    public void userRetrievesTopJobApplyLinks(int topJobs) {
        naukriPage.filterJobListings();
        List<String> applyLinks = naukriPage.getTop10ApplyLinks();
        System.out.println("\n========== TOP " + topJobs + " JOB POSTINGS ==========\n");
        for (String jobInfo : applyLinks) {
            System.out.println(jobInfo);
        }
        System.out.println("========== END OF JOB LIST ==========\n");
        System.out.println("Total jobs retrieved: " + applyLinks.size());
    }

    @Given("navigate to naukri and search for skill {string}, experience {string}, location {string}")
    public void navigateToNaukriAndSearchForSkillExperienceLocation(String skill, String experince, String location) {
        naukriPage.executeCompleteWorkflow(skill, experince, location,"lallubanala444@gmail.com");
    }

    @Given("navigate to naukri and search jobs")
    public void navigateToNaukriAndSearchJobs() {
        naukriPage.executeCompleteWorkflow(Property.Role, Property.Experince, Property.Location,Property.Mail);
    }

    @Given("navigate to naukri and search jobs from API")
    public void navigateToNaukriAndSearchJobsFromAPI() {
        naukriApiPage.executeCompleteWorkflow(Property.Role, Property.Experince, Property.Location,Property.Mail);
    }

    @Then("print the properties to console")
    public void print_the_properties_to_console() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("read {string} from config file")
    public void readFromConfigFile(String reader) {
        String prop= Property.platform;
        System.out.println("This is the property: "+prop);
    }
    @Then("update the bio")
    public void update_the_bio() {
        naukriPage.updateProfile();
    }

    @And("login to site using {string} and {string}")
    public void loginToSite(String UserName,String Password) {
        naukriPage.loginToSite(UserName,Password);
    }
}
