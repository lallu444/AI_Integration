package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected WebElement waitForElementToBeVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementToBeVisible(WebElement locator) {
        return wait.until(ExpectedConditions.visibilityOf(locator));
    }

    protected WebElement waitForElementToBeVisible(WebElement locator, long timeout) {
        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait1.until(ExpectedConditions.visibilityOf(locator));
    }

    protected WebElement waitForElementToBeClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitForElementToBeClickable(WebElement locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForElementToBeClickable(locator).click();
    }

    protected void click(WebElement locator) {
        waitForElementToBeVisible(locator);
        locator.click();
    }

    protected void sendKeys(By locator, String text) throws IOException {
        scrollToElementBy(locator);
        waitForElementToBeVisible(locator).sendKeys(text);
    }

    protected void sendKeys(WebElement locator, String text) {
        locator.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForElementToBeVisible(locator).getText();
    }

    protected void selectByValue(By locator, String value) {
        Select select = new Select(waitForElementToBeVisible(locator));
        select.selectByValue(value);
    }

    protected void selectByVisibleText(By locator, String text) {
        Select select = new Select(waitForElementToBeVisible(locator));
        select.selectByVisibleText(text);
    }

    protected void clearAndSendKeys(By locator, String text) {
        WebElement element = waitForElementToBeVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void scrollToElement(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void scrollToElementBy(By elementPath) throws IOException {
        // 1. Capture screenshot as a file
        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // 2. Save the file to your desired directory
        File destination = new File("screenshot.png");
        FileHandler.copy(source, destination);
        waitForElementToBeVisible(elementPath);
        WebElement element =driver.findElement(elementPath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Step 1: Scroll to element
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        // Step 2: Scroll down 150px
        js.executeScript("window.scrollBy(0, 150);");
    }

    protected void clickjavascript(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}