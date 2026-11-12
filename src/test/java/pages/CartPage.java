package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutStepOnePage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutStepOnePage(driver);
    }
}
