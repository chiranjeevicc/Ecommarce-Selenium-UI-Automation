package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutStepTwoPage extends BasePage {

    private final By finishButton = By.id("finish");
    private final By completeHeader = By.cssSelector(".complete-header");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    public void finishOrder() {
        click(finishButton);
        wait.until(ExpectedConditions.urlContains("checkout-complete.html"));
    }

    public String getCompletionMessage() {
        return getText(completeHeader);
    }
}
