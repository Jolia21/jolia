package com.prestashop;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class BaseTe {

  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;
  protected static TestData testData = new TestData();

  @BeforeEach
  public void setUp() {
    playwright = Playwright.create();

    browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions().setHeadless(false));

    context = browser.newContext(
        new Browser.NewContextOptions().setViewportSize(1280, 720));

    page = context.newPage();

    page.setDefaultTimeout(60000);
    page.setDefaultNavigationTimeout(60000);
  }



  private void takeScreenshot(String testName) {
    try {
      Files.createDirectories(Path.of("screenshots"));

      page.screenshot(new Page.ScreenshotOptions()
          .setPath(Path.of("screenshots/" + testName + ".png"))
          .setFullPage(true));

    } catch (Exception e) {
      System.out.println("Screenshot failed: " + e.getMessage());
    }
    
  }
  @AfterEach
  void tearDown() {
      try {
          Files.createDirectories(Path.of("screenshots"));
          page.screenshot(new Page.ScreenshotOptions()
                  .setPath(Path.of("screenshots/" + System.currentTimeMillis() + ".png"))
                  .setFullPage(true));
      } catch (Exception ignored) {}

      try { if (context != null) context.close(); } catch (Exception ignored) {}
      try { if (browser != null) browser.close(); } catch (Exception ignored) {}
      try { if (playwright != null) playwright.close(); } catch (Exception ignored) {}
  }


}
