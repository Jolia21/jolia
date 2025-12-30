package com.prestashop;

import com.microsoft.playwright.Frame;
import org.junit.jupiter.api.Test;

public class LoginTest extends BaseTe {

  @Test
  public void test2_login_with_credentials_from_test1() {

    //  nese TestData 2shtë bosh, krijo user tani
    if (TestData.email == null || TestData.email.isBlank()
        || TestData.password == null || TestData.password.isBlank()) {

      HomePage home = new HomePage(page);
      home.open();

      Frame shop = home.shopFrame();
      home.waitShopReady(shop);
      home.closePopupsIfAny(shop);

      LoginPage lp = new LoginPage(shop);
      lp.openLogin();
      lp.openRegisterDirect();

      RegisterPage reg = new RegisterPage(shop);
      reg.assertRegisterTitleOrHeading();
      reg.assertFormOpened();

      TestData.email = "tarazhijolia+" + System.currentTimeMillis() + "@gmail.com";
      TestData.password = "Jolia123!";

      reg.fillMandatoryAndSubmit("Jolia", "Tarazhi", TestData.email, TestData.password);
      reg.assertLoggedIn();
      reg.logoutAndAssertSignInVisible();
    }

   
    HomePage home = new HomePage(page);
    home.open();

    Frame shop = home.shopFrame();
    home.waitShopReady(shop);
    home.closePopupsIfAny(shop);

    LoginPage loginPage = new LoginPage(shop);
    loginPage.openLogin();

    
    page.locator("iframe#framelive").waitFor();
    shop = page.frame("framelive");
    loginPage = new LoginPage(shop);

    System.out.println("LOGIN email = " + TestData.email);
    System.out.println("LOGIN pass  = " + TestData.password);

    loginPage.login(TestData.email, TestData.password);

   
    RegisterPage registerPage = new RegisterPage(shop);
    registerPage.assertLoggedIn();
  }
}
