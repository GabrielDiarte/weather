package pageobject;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {

    private final long TIME_LIMIT = 15;
    private WebDriver driver;
    private WebDriverWait waitDriver;
    private JavascriptExecutor jsExecutor;

    private final static Logger log = Logger.getLogger(BasePage.class);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitDriver = new WebDriverWait(driver, Duration.ofSeconds(TIME_LIMIT));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    protected void navigateToURL(String url) {
        driver.get(url);
    }

    public void waitUntilPageLoaded() {
        waitDriver.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        });
    }

    public void waitUntilElementIsDisplayed(WebElement element) {
        waitDriver.until(ExpectedConditions.visibilityOf(element));
    }

    public boolean checkElementIsClickable(WebElement element) {
        boolean isClickable = false;
        int time = 0;
        while (time < TIME_LIMIT) {
            try {
                isClickable = waitDriver.until(ExpectedConditions.elementToBeClickable(element)) != null;
                break;
            } catch (StaleElementReferenceException e) {
                element = redefineElement(element);
                wait(2000);
                time += 2;
            }
        }
        return isClickable;
    }

    public void click(WebElement element) {
        int time = 0;
        boolean clickDone = false;

        while (clickDone == false && time < TIME_LIMIT) {
            try {
                if (checkElementIsClickable(element)) {
                    element.click();
                    clickDone = true;
                }
            } catch (ElementNotInteractableException e) {
                log.info("Exception controlled in click method:" + e.getMessage());
                wait(2000);
                time += 2;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                element = redefineElement(element);
                wait(2000);
                time += 2;
            }
        }
        if (clickDone == false && time == TIME_LIMIT) {
            throw new ElementClickInterceptedException("Error when clicking, timeout exeeced.");
        }
    }

    public void scrollAndClick(WebElement element) {
        int time = 0;
        boolean clickDone = false;

        while (!clickDone && time < TIME_LIMIT) {
            try {
                clickDone = true;
                scrollToElement(element);
                element.click();
            } catch (StaleElementReferenceException
                     | NoSuchElementException
                     | ElementClickInterceptedException e) {
                log.info("Exception controlled in scroll and click method:" + e.getMessage());
                wait(2000);
                time += 2;
                clickDone = false;
            }
        }

        if (!clickDone && time >= TIME_LIMIT) {
            throw new ElementClickInterceptedException("Error trying to scrollAndClick method");
        }
    }

    public void scrollAndClickJs(WebElement element) {
        int time = 0;
        boolean clickDone = false;

        while (!clickDone && time < TIME_LIMIT) {
            try {
                clickDone = true;
                scrollToElementJs(element);
                element.click();
            } catch (StaleElementReferenceException
                     | NoSuchElementException
                     | ElementClickInterceptedException e) {
                log.info("Exception controlled in scroll and click method:" + e.getMessage());
                wait(2000);
                time += 2;
                clickDone = false;
            }
        }

        if (!clickDone && time >= TIME_LIMIT) {
            throw new ElementClickInterceptedException("Error trying to scrollAndClick method");
        }
    }

    public void sendKeys(WebElement element, CharSequence text) {
        boolean filled = false;
        int time = 0;

        while (filled == false && time < TIME_LIMIT) {

            try {
                if (checkElementIsClickable(element)) {
                    element.sendKeys(text);
                    filled = true;
                }
            } catch (ElementNotInteractableException e) {
                log.info("Exception controlled in sendKeys method.");
                wait(2000);
                time += 2;
            } catch (StaleElementReferenceException e) {
                element = redefineElement(element);
                wait(2000);
                time += 2;
            }
        }
        if (!filled) {
            throw new ElementNotInteractableException("Unable to send keys to the element: " + element);
        }
    }

    public void wait(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            log.error("Exception controlled in wait method.");
        }
    }

    public WebElement findElement(By by) {
        WebElement element = null;
        int attempts = 0;
        while (attempts < 5) {
            try {
                element = driver.findElement(by);
                element.isEnabled();
                break;
            } catch (org.openqa.selenium.NoSuchElementException
                     | org.openqa.selenium.StaleElementReferenceException e) {
                log.info("Exception controlled in findElement method");
            }
            wait(5000);
            attempts++;
        }
        if (attempts == 5 && element == null) {
            throw new NoSuchElementException("Element not found in findElement method");
        }
        return element;
    }

    public WebElement redefineElement(WebElement element) {
        WebElement newElement;
        if (element.toString().contains("xpath")) {
            newElement = findElement(By.xpath(getXPathFromElement(element)));
        } else {
            newElement = null;
        }
        log.info("Element redefined");
        return newElement;
    }

    public boolean checkExist(WebElement element) {
        boolean exist = false;
        int time = 0;

        while (!exist && time < 4) {
            try {
                exist = element.isDisplayed();
            } catch (NoSuchElementException e) {
                wait(2000);
                time += 2;
            }
        }
        return exist;
    }

    public String getXPathFromElement(WebElement element) {
        String xpath = "";
        String[] pathVariables = element.toString().split(" -> xpath: ");
        for (int i = 1; i < pathVariables.length; i++) {
            String temp = pathVariables[i];
            if (temp.substring(0, 1).equals(".")) {
                temp = temp.substring(1, temp.length());
            }

            int countOpenBracket = temp.length() - temp.replace("[", "").length();
            int countCloseBracket = temp.length() - temp.replace("]", "").length();
            int difference = countCloseBracket - countOpenBracket;
            if (difference != 0) {
                xpath += temp.substring(0, temp.length() - difference);
            } else {
                xpath += temp;
            }
        }
        return xpath;
    }

    public void scrollToElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    public void scrollToElementJs(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({ block: 'center', inline: 'nearest' });",
                element);
    }
}
