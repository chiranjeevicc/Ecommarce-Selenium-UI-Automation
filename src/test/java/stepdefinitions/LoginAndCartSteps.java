package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.CartPage;
import pages.CheckoutStepOnePage;
import pages.CheckoutStepTwoPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DriverFactory;

import java.util.List;

public class LoginAndCartSteps {

    private final LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutStepTwoPage checkoutStepTwoPage;

    @Given("the user is on the SauceDemo login page")
    public void the_user_is_on_the_login_page() {
        loginPage.open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in(String username, String password) {
        inventoryPage = loginPage.loginAs(username, password);
    }

    @Then("the user should land on the products page")
    public void the_user_should_land_on_products_page() {
        Assert.assertTrue(inventoryPage.getPageTitle().equalsIgnoreCase("Products"),
                "Expected to land on Products page after login");
    }

    @Then("the page title should be {string}")
    public void the_page_title_should_be(String expectedTitle) {
        Assert.assertEquals(inventoryPage.getPageTitle(), expectedTitle);
    }

    @Then("an error message containing {string} should be displayed")
    public void an_error_message_should_be_displayed(String expectedMessage) {
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message to be shown");
        Assert.assertTrue(loginPage.getErrorMessage().contains(expectedMessage),
                "Expected error to contain: " + expectedMessage + " but was: " + loginPage.getErrorMessage());
    }

    @When("the user adds the following products to the cart:")
    public void the_user_adds_products_to_cart(List<String> products) {
        for (String product : products) {
            inventoryPage.addProductToCart(product);
        }
    }

    @Given("the user adds {string} to the cart")
    public void the_user_adds_single_product_to_cart(String product) {
        inventoryPage.addProductToCart(product);
    }

    @When("the user removes {string} from the cart")
    public void the_user_removes_product_from_cart(String product) {
        inventoryPage.removeProductFromCart(product);
    }

    @Then("the cart badge count should be {string}")
    public void the_cart_badge_count_should_be(String expectedCount) {
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), expectedCount);
    }

    @When("the user proceeds to checkout with first name {string}, last name {string}, zip {string}")
    public void the_user_proceeds_to_checkout(String firstName, String lastName, String zip) {
        cartPage = inventoryPage.goToCart();
        CheckoutStepOnePage stepOne = cartPage.proceedToCheckout();
        checkoutStepTwoPage = stepOne.fillInfoAndContinue(firstName, lastName, zip);
        checkoutStepTwoPage.finishOrder();
    }

    @Then("the order should be completed successfully")
    public void the_order_should_be_completed() {
        Assert.assertEquals(checkoutStepTwoPage.getCompletionMessage(), "Thank you for your order!");
    }
}
