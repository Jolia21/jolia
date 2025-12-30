package com.prestashop;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AccountPage {
  private final Page page;

  public AccountPage(Page page) { this.page = page; }

  private FrameLocator frame() {
    return page.frameLocator("iframe#framelive");
  }

  public AccountPage assertLoggedIn() {
    assertThat(frame().locator("a.logout, div.user-info a:has-text('Sign out')")).isVisible();
    return this;
  }

  public void signOut() {
    frame().locator("a.logout, div.user-info a:has-text('Sign out')").click();
 // Kthehu tek faqja e hyrje  , "Sign in" duhet të shfaqet perseri
    assertThat(frame().locator("div.user-info a:has-text('Sign in')")).isVisible();
  }
}
