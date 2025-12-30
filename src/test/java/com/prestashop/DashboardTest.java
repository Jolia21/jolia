package com.prestashop;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class DashboardTest {

  static Playwright playwright;
  static Browser browser;
  static BrowserContext context;
  static Page page;

  @BeforeAll
  static void setUpAll() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1200, 700));
    page = context.newPage();
    page.setDefaultTimeout(40000);
    page.setDefaultNavigationTimeout(40000);
  }

  @AfterAll
  static void tearDownAll() {
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @Test
  void testFullUserJourney_TC3_and_TC4() {
    // 1) Navigate to PrestaShop demo
    page.navigate("https://demo.prestashop.com");

    // Sigurohu qe  iframe loaded
    Frame shop = reacquireFrame(page);

    // Close possible popups inside iframe
    if (shop.locator("button:has-text('Close')").count() > 0) {
      try { shop.locator("button:has-text('Close')").first().click(); } catch (Exception ignored) {}
    }
    if (shop.locator("button[aria-label='Close']").count() > 0) {
      try { shop.locator("button[aria-label='Close']").first().click(); } catch (Exception ignored) {}
    }

    // shko tek  Login page
    shop.locator("div.user-info a:has-text('Sign in')").click();
    shop = reacquireFrame(page);

    // shko tek  Create account
    shop.locator("a:has-text('Create one here')").click();
    shop = reacquireFrame(page);

    // Register
    RegisterPage register = new RegisterPage(shop);
    register.assertRegisterTitleOrHeading();
    register.assertFormOpened();

    TestData.email = "tarazhijolia" + System.currentTimeMillis() + "@gmail.com";
    TestData.password = "Jolia123!";

    register.fillMandatoryAndSubmit("Jolia", "Tarazhi", TestData.email, TestData.password);
    register.assertLoggedIn();

    // Dashboard actions (iframe-safe DashboardPage)
    DashboardPage dashboard = new DashboardPage(page, "framelive");

    dashboard.hoverAccessoriesAndClickHomeAccessories();
    dashboard.assertOnHomeAccessoriesPage();

    dashboard.sortByNameAToZ();

    dashboard.assertProductCount(8);
    dashboard.checkPolyesterFilter();
    dashboard.assertProductCount(3);
    dashboard.uncheckPolyesterFilter();
    dashboard.assertProductCount(8);

    // Shto 2 produkte  tek wishlist 
    dashboard.addProductToWishlistByIndex(1);
    dashboard.addProductToWishlistByIndex(2);

    dashboard.goToMyWishlistFromProfile();
    dashboard.assertWishlistItemsCountInTitle(2);

    // Back to Home Accessories before cart steps
    dashboard.hoverAccessoriesAndClickHomeAccessories();
    dashboard.assertOnHomeAccessoriesPage();

    // shto  3 products to cart 
    dashboard.addProductToCartByIndex(2);
    dashboard.addProductToCartByIndex(4);
    dashboard.addProductToCartByIndex(6);

    
    dashboard.assertCartCount(3);

     //TC 4

    System.out.println("TC4 - Step 1: Navigated to Shopping Cart");
    dashboard.goToShoppingCart();

    System.out.println("TC4 - Step 2: Shopping Cart page verified");
    dashboard.assertOnShoppingCartPage();

    System.out.println("TC4 - Step 3: Buttons verified");
    dashboard.assertCartButtonsDisplayed();

    System.out.println("TC4 - Step 4: Total price verified");
    dashboard.assertCartTotalIsCorrect();

    System.out.println("TC4 - ALL STEPS PASSED");
    
 // TC 5

    System.out.println("TC5 - Step 1&2: Delete first item and verify count decreases");
    int before = dashboard.getCartItemsCount();
    dashboard.deleteFirstItemFromCartAndAssertDecreased();
    System.out.println("TC5 - Count: " + before + " -> " + dashboard.getCartItemsCount());

    System.out.println("TC5 - Step 3: Repeat delete until cart is empty");
    dashboard.deleteAllItemsUntilCartEmpty();

    System.out.println("TC5 - Step 4: Verify cart is empty");
    dashboard.assertShoppingCartIsEmpty();

    System.out.println("TC5 - ALL STEPS PASSED");

  }
  


  static Frame reacquireFrame(Page page) {
    page.locator("iframe#framelive").waitFor();
    Frame f = page.frame("framelive");
    if (f == null) throw new AssertionError("Frame 'framelive' not found");
    return f;
  }
}
