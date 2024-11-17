package test;

import io.qameta.allure.Description;
import org.junit.Assert;
import org.testng.annotations.Test;
import pageobject.WeatherPage;

public class WeatherTest extends BaseTest {

    private WeatherPage weatherPage;

    @Test(description = "Example test")
    @Description("Example test")
    public void test(){
        weatherPage = new WeatherPage(getDriver());
        weatherPage.navigateWeather();
        weatherPage.acceptCookies();
        weatherPage.searchTown("Madrid");
        weatherPage.clickTownByName("Las Rozas de Madrid");
        weatherPage.clickHourButton();
        weatherPage.clickHour("19:00");
        Assert.assertTrue("More info list is not displayed", weatherPage.moreInfoListIsDisplayed());
    }

}
