package pageobject;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WeatherPage extends BasePage {
    private final String urlWeather = " https://www.eltiempo.es/";

    @FindBy(xpath = "//a[@type='button' and text()='Agree & continue']")
    private WebElement buttonAcceptCookies;
    @FindBy(xpath = "//input[@id='term']")
    private WebElement searchBar;
    @FindBy(xpath = "//section[@class='nav forecast ']//a[text()='Horas']")
    private WebElement hourButton;
    @FindBy(xpath = "//h3[contains(text(),'El tiempo Mañana')]")
    private WebElement tomorrowWeather;
    @FindBy(xpath = "//tr[not(@hidden)]//div[@class='more-info-list']")
    private WebElement moreInfoList;


    public WeatherPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Step("Navega la web del tiempo")
    public void navigateWeather() {
        navigateToURL(urlWeather);
    }

    @Step("Aceptar cookies")
    public void acceptCookies() {
        waitUntilElementIsDisplayed(buttonAcceptCookies);
        click(buttonAcceptCookies);
    }

    @Step("Busca el municipio")
    public void searchTown(String town) {
        waitUntilPageLoaded();
        sendKeys(searchBar, town);
    }

    @Step("Click en el municipio por nombre")
    public void clickTownByName(String town) {
        waitUntilPageLoaded();
        String dynamicPath = "//p[@class='poi-title' and text()='"+town+"']";
        scrollAndClickJs(findElement(By.xpath(dynamicPath)));
    }

    @Step("Click en la hora elegida")
    public void clickHour(String selectedHour) {
        waitUntilPageLoaded();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime actualTime = LocalTime.now();
        LocalTime selectedTime = LocalTime.parse(selectedHour, formatter);

        if (actualTime.isAfter(selectedTime)){
            scrollAndClickJs(tomorrowWeather);
        }

        String dynamicPath = "(//p[@class='time' and text()='"+ selectedHour +"'])[1]";
        scrollAndClickJs(findElement(By.xpath(dynamicPath)));
    }

    @Step("Click boton horas")
    public void clickHourButton() {
        waitUntilPageLoaded();
        scrollAndClickJs(hourButton);
    }

    public boolean moreInfoListIsDisplayed() {
        return checkExist(moreInfoList);
    }

}
