package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class DriverWeb {
    private Duration implicitWait;
    private WebDriver driver;

    public DriverWeb() {
        implicitWait = Duration.ofSeconds(15);
    }

    public WebDriver initDriver() {

        driver = initChrome();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(implicitWait);
        driver.manage().timeouts().implicitlyWait(implicitWait);

        return driver;
    }

    private WebDriver initChrome() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("start-maximized");

        WebDriver driverTest = new ChromeDriver(options);

        return driverTest;
    }

}
