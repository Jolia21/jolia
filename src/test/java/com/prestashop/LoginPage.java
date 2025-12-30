package com.prestashop;

import com.microsoft.playwright.Frame;

public class LoginPage {

  private final Frame shop;
  private TestData testData;

  public LoginPage(Frame shop) {
    this.shop = shop;
  }

  public void openLogin() {
    shop.click("a:has-text('Sign in')");
  }

  public void openRegisterDirect() {
    shop.click("a:has-text('Create one here')");
  }

  public void login(String email, String password) {
	  if (email == null || email.isBlank())
	    throw new IllegalArgumentException("Login email is null/blank. Test data not set.");
	  if (password == null || password.isBlank())
	    throw new IllegalArgumentException("Login password is null/blank. Test data not set.");

	  
	  shop.locator("form#login-form").waitFor();

	  shop.locator("form#login-form #field-email").click();
	  shop.locator("form#login-form #field-email").fill(email);

	  shop.locator("form#login-form #field-password").click();
	  shop.locator("form#login-form #field-password").fill(password);

	  
	  String typed = shop.locator("form#login-form #field-email").inputValue();
	  if (!email.equals(typed)) throw new IllegalStateException("Email did not get typed. Got: " + typed);

	  shop.locator("form#login-form button#submit-login").click();
	}


}