package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Central place that creates and configures the WebDriver instance.
 * Selenium 4's built-in Selenium Manager resolves the correct browser driver
 * binary automatically -- no manual chromedriver/geckodriver downloads needed,
 * which matters on Intel Mac (x86_64) where mismatched driver architectures
 * used to be a common headache.
 *
 * Browser and headless mode are controlled via system properties so the same
 * code runs the same way locally (visible browser for debugging) and in
 * GitHub Actions (headless, no display).
 *
 * Usage:
 *   mvn test                                   -> chrome, headed (local default)
 *   mvn test -Dbrowser=firefox                 -> firefox, headed
 *   mvn test -Dheadless=true                   -> chrome, headless
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver();
        }
        return driverThreadLocal.get();
    }

    private static void initDriver() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver driver;
        switch (browser) {
            case "firefox":
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (headless) {
                    ffOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(ffOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
