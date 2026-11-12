package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutStepOnePage extends BasePage {

    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By zipField = By.id("postal-code");
    private final By continueButton = By.id("continue");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepTwoPage fillInfoAndContinue(String firstName, String lastName, String zip) {
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(zipField, zip);
        click(continueButton);
        wait.until(ExpectedConditions.urlContains("checkout-step-two.html"));
        return new CheckoutStepTwoPage(driver);
    }
}
