@login @cart
Feature: Login and Shopping Cart
  As a registered user of SauceDemo
  I want to log in and add products to my cart
  So that I can proceed to checkout

  Background:
    Given the user is on the SauceDemo login page

  @smoke @positive
  Scenario: Successful login with valid standard user
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the user should land on the products page
    And the page title should be "Products"

  @negative
  Scenario Outline: Login fails with invalid credentials
    When the user logs in with username "<username>" and password "<password>"
    Then an error message containing "<errorMessage>" should be displayed

    Examples:
      | username        | password        | errorMessage                                    |
      | locked_out_user  | secret_sauce    | Sorry, this user has been locked out             |
      | standard_user    | wrong_password  | Username and password do not match               |
      | invalid_user     | secret_sauce    | Username and password do not match               |

  @regression
  Scenario: Add multiple products to cart and verify cart badge count
    Given the user logs in with username "standard_user" and password "secret_sauce"
    When the user adds the following products to the cart:
      | Sauce Labs Backpack       |
      | Sauce Labs Bike Light     |
      | Sauce Labs Bolt T-Shirt   |
    Then the cart badge count should be "3"
    When the user removes "Sauce Labs Bike Light" from the cart
    Then the cart badge count should be "2"

  @regression
  Scenario: Checkout flow completes with a valid order
    Given the user logs in with username "standard_user" and password "secret_sauce"
    And the user adds "Sauce Labs Backpack" to the cart
    When the user proceeds to checkout with first name "Jackie", last name "QA", zip "560001"
    Then the order should be completed successfully
