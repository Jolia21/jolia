package com.prestashop;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterPage {
  private final Frame shop;

  
  private final Locator customerForm;

  public RegisterPage(Frame shop) {
    this.shop = shop;
   
    this.customerForm = shop.locator("form#customer-form");
  }

  
  private Locator headingH1() { return shop.locator("h1"); }

  private Locator genderMr() { return customerForm.locator("input[name='id_gender'][value='1']"); }
  private Locator firstName() { return customerForm.locator("input[name='firstname']"); }
  private Locator lastName()  { return customerForm.locator("input[name='lastname']"); }

  
  private Locator email()     { return customerForm.locator("input[name='email']"); }
  private Locator password()  { return customerForm.locator("input[name='password']"); }

  private Locator gdpr()      { return customerForm.locator("input[name='psgdpr']"); }
  private Locator submit()    { return customerForm.locator("button[type='submit']"); }

  private Locator signOut() { return shop.locator("a.logout, div.user-info a:has-text('Sign out')"); }
  private Locator signIn()  { return shop.locator("div.user-info a:has-text('Sign in')"); }

  
  public void assertRegisterTitleOrHeading() {
    headingH1().waitFor();
    String h = headingH1().innerText().toLowerCase();
    assertTrue(h.contains("create") || h.contains("account"),
        "Register heading not found. Actual h1: " + headingH1().innerText());
  }

  public void assertFormOpened() {
    customerForm.waitFor();
    firstName().waitFor();
    assertTrue(firstName().isVisible(), "Register form not opened (firstname not visible).");
  }



private Locator termsCheckbox() {

 return customerForm.locator(
     "label:has-text('I agree to the terms') input[type='checkbox'], input[name='psgdpr']"
 ).first();
}

private Locator customerPrivacyCheckbox() {

 return customerForm.locator(
     "label:has-text('Customer data privacy') input[type='checkbox'], input[name='customer_privacy']"
 ).first();
}

private void checkIfVisible(Locator checkbox) {
 if (checkbox.count() > 0) {
   checkbox.scrollIntoViewIfNeeded();
   
   checkbox.setChecked(true);
 }
}

public void fillMandatoryAndSubmit(String fName, String lName, String mail, String pass) {
 if (genderMr().count() > 0) genderMr().check();

 firstName().fill(fName);
 lastName().fill(lName);
 email().fill(mail);
 password().fill(pass);


 checkIfVisible(termsCheckbox());
 checkIfVisible(customerPrivacyCheckbox());

 submit().scrollIntoViewIfNeeded();
 submit().click();
}


  public void assertLoggedIn() {
    signOut().waitFor();
    assertTrue(signOut().isVisible(), "User is NOT logged in (Sign out not visible).");
  }

  public void logoutAndAssertSignInVisible() {
    signOut().click();
    signIn().waitFor();
    assertTrue(signIn().isVisible(), "Sign in not visible after logout.");
  }
}