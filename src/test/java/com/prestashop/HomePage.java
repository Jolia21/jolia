package com.prestashop;

import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

public class HomePage {
  private final Page page;

  public HomePage(Page page) {
    this.page = page;
  }

  public void open() {
    page.navigate(
        "https://demo.prestashop.com/",
        new Page.NavigateOptions()
            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
            .setTimeout(60000)
    );
  }

  public Frame shopFrame() {
    // iframe kryesor te demo prestashop
    return page.frame("framelive");
  }

  public void waitShopReady(Frame shop) {
    
    shop.waitForLoadState();
    shop.waitForSelector(
        "body",
        new Frame.WaitForSelectorOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(60000)
    );
  }

  public void closePopupsIfAny(Frame shop) {
    
    if (shop.querySelector("button:has-text('Close')") != null) {
      shop.click("button:has-text('Close')");
    }

    
    if (shop.querySelector("#popup-close") != null) {
      shop.click("#popup-close");
    }
  }
}