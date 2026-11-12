@checkout @regression
Feature: Sauce Labs Onesie checkout
  As a registered SauceDemo user
  I want to purchase the Sauce Labs Onesie
  So that I can verify the checkout flow

  Background:
    Given the user is on the SauceDemo login page

  Scenario: Complete checkout for the Sauce Labs Onesie
    Given the user logs in with username "standard_user" and password "secret_sauce"
    And the user adds "Sauce Labs Onesie" to the cart
    When the user proceeds to checkout with first name "chiru", last name "mys", zip "570001"
    Then the order should be completed successfully
