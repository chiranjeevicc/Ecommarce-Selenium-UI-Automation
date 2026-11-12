package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class InventoryPage extends BasePage {

    private final By pageTitle = By.cssSelector(".title");
    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By inventoryItemName = By.cssSelector(".inventory_item_name");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    private By addToCartButtonFor(String productName) {
        // SauceDemo button ids follow pattern: add-to-cart-<slugified-product-name>
        String slug = productName.toLowerCase().replace(" ", "-");
        return By.id("add-to-cart-" + slug);
    }

    private By removeButtonFor(String productName) {
        String slug = productName.toLowerCase().replace(" ", "-");
        return By.id("remove-" + slug);
    }

    public void addProductToCart(String productName) {
        click(addToCartButtonFor(productName));
    }

    public void removeProductFromCart(String productName) {
        By removeButton = removeButtonFor(productName);
        click(removeButton);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(removeButton));
    }

    public String getCartBadgeCount() {
        if (!isDisplayed(cartBadge)) {
            return "0";
        }
        return getText(cartBadge);
    }

    public CartPage goToCart() {
        click(cartLink);
        return new CartPage(driver);
    }
}
