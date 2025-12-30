package com.prestashop;

import com.microsoft.playwright.Frame;
import org.junit.jupiter.api.Test;

public class RegisterTest extends BaseTe {

  @Test
  public void test1_registerNewUser_and_logout() {
    // open the site , go to Sign in, me pas  Create account 
    page.navigate("https://demo.prestashop.com/");

    
    page.locator("iframe#framelive").waitFor();
    Frame shop = page.frame("framelive");

    
    if (shop.locator("button:has-text('Close')").count() > 0) {
      try { shop.locator("button:has-text('Close')").first().click(); } catch (Exception ignored) {}
    }
    if (shop.locator("button[aria-label='Close']").count() > 0) {
      try { shop.locator("button[aria-label='Close']").first().click(); } catch (Exception ignored) {}
    }

    // shko tek  Login page
    shop.locator("div.user-info a:has-text('Sign in')").click();

    
    page.locator("iframe#framelive").waitFor();
    shop = page.frame("framelive");

    
    shop.locator("a:has-text('Create one here')").click();

    
    page.locator("iframe#framelive").waitFor();
    shop = page.frame("framelive");

    
    RegisterPage register = new RegisterPage(shop);

    register.assertRegisterTitleOrHeading();
    register.assertFormOpened();
    TestData testData = new TestData();
    testData.email = "tarazhijolia+" + System.currentTimeMillis() + "@gmail.com";
    testData.password = "Jolia123!";

    register.fillMandatoryAndSubmit("Jolia", "Tarazhi", testData.email, testData.password);

    // verifiko  logged in + logout 
    register.assertLoggedIn();
    register.logoutAndAssertSignInVisible();


  }
}