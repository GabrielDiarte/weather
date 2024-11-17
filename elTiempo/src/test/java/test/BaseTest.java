package test;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pageobject.BasePage;
import utils.DriverWeb;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BaseTest {
    private final static Logger log = Logger.getLogger(BasePage.class);

    private DriverWeb driverWeb;
    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeMethod
    public void setUp() {
        driverWeb = new DriverWeb();
        driver = driverWeb.initDriver();
    }

    @AfterMethod
    public void tearDown() {
        takeScreenshot();
        driver.quit();
    }

    public void takeScreenshot() {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd", new Locale("es", "ES")));
        String time = dateTime.format(DateTimeFormatter.ofPattern("HHmmss", new Locale("es", "ES")));

        try {
            FileUtils.copyFile(scrFile, new File(".\\screenshots\\screenshot" + date + time + ".png"));
        } catch (IOException e) {
            log.error("Error taking the screenshot " + e.getMessage());
        }
    }
}
