package com.prestashop;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardPage {

  private final Page page;
  private final String frameName;

  public DashboardPage(Page page, String frameName) {
    this.page = page;
    this.frameName = frameName;
  }

  private Frame shop() {
    page.locator("iframe#framelive").waitFor();
    Frame f = page.frame(frameName);
    if (f == null) throw new AssertionError("Shop frame not found: " + frameName);
    return f;
  }

 

  private void waitUntil(BooleanSupplier condition, int timeoutMs) {
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis() - start < timeoutMs) {
      try {
        if (condition.getAsBoolean()) return;
      } catch (Exception ignored) {}
      page.waitForTimeout(200);
    }
    throw new AssertionError("Timeout waiting for condition");
  }

  private void waitForGridReady() {
    shop().locator("div.products")
        .waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(10_000));
  }

 // navigatiom

  public void hoverAccessoriesAndClickHomeAccessories() {
    shop().locator("#category-6 > a").hover();
    shop().locator("#category-8 > a").click();

    shop().locator("h1:has-text('Home Accessories')")
        .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    waitForGridReady();
  }

  public void assertOnHomeAccessoriesPage() {
    assertTrue(shop().locator("h1:has-text('Home Accessories')").isVisible(),
        "Not on Home Accessories page");
  }

  //sorting

  public void sortByNameAToZ() {
    waitForGridReady();
    String before = firstProductName();

    shop().locator("button:has-text('Relevance')").click();
    shop().locator("a:has-text('Name, A to Z')").click();

    waitUntil(() -> !firstProductName().equals(before), 10_000);
  }

  private String firstProductName() {
    Locator name = shop().locator("article.product-miniature h2 a").first();
    name.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    return name.innerText().trim();
  }

  //Product count

  public void assertProductCount(int expected) {
    waitUntil(() -> shop().locator("article.product-miniature").count() == expected, 10_000);
    assertEquals(expected,
        shop().locator("article.product-miniature").count(),
        "Unexpected product count");
  }

  // Filters 

  public void checkPolyesterFilter() {
    Locator polyester = shop().locator("label:has-text('Polyester')");
    int before = shop().locator("article.product-miniature").count();
    polyester.scrollIntoViewIfNeeded();
    polyester.click();
    waitUntil(() -> shop().locator("article.product-miniature").count() != before, 10_000);
  }

  public void uncheckPolyesterFilter() {
    checkPolyesterFilter();
  }

  // Wishlist 

  public void addProductToWishlistByIndex(int index) {
    waitForGridReady();
    Locator product = shop().locator("article.product-miniature").nth(index);
    product.scrollIntoViewIfNeeded();
    product.hover();
    product.locator("button.wishlist-button-add").click();

    Locator myWishlist = shop().locator("li.wishlist-list-item p:has-text('My wishlist')");
    myWishlist.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    myWishlist.click();
  }

  public void goToMyWishlistFromProfile() {
    shop().locator("a.account").click();
    shop().locator("a:has-text('My wishlists')").click();

    shop().locator("h1:has-text('My wishlists')")
        .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    shop().locator("a:has-text('My wishlist')").first().click();

    shop().locator("h1:has-text('My wishlist')")
        .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
  }

  public void assertWishlistItemsCountInTitle(int expected) {
    Locator count = shop().locator("text=(" + expected + ")").first();
    count.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    assertTrue(count.isVisible(), "Wishlist count mismatch");
  }

  

  public void addProductToCartByIndex(int index) {
    waitForGridReady();

    Locator product = shop().locator("article.product-miniature").nth(index);
    product.scrollIntoViewIfNeeded();
    product.hover();

    product.locator("a.quick-view").click();

    Locator quickView = shop().locator(".modal.quickview");
    quickView.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    quickView.locator("button[data-button-action='add-to-cart']").click();

    Locator cartModal = shop().locator("#blockcart-modal");
    cartModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    cartModal.locator("button:has-text('Continue shopping')").click();
    cartModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
  }

  // TC3
  public void assertCartCount(int expected) {
    Locator cart = shop().locator("div#_desktop_cart a").first();
    cart.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    waitUntil(() -> cart.innerText().contains("(" + expected + ")"), 12_000);

    assertTrue(cart.innerText().contains("(" + expected + ")"),
        "Cart count mismatch. Actual: " + cart.innerText());
  }

  // TC 4

  public void goToShoppingCart() {
    Locator cartLink = shop().locator("div#_desktop_cart a").first();
    cartLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    try { cartLink.click(); } catch (Exception ignored) {}

    // nese ende nuk esht shtuar ne karte , force click
    if (!shop().locator("h1").first().innerText().toLowerCase().contains("shopping cart")) {
      cartLink.click(new Locator.ClickOptions().setForce(true));
    }

    shop().locator("h1:has-text('SHOPPING CART'), h1:has-text('Shopping Cart')")
        .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15_000));
  }

  public void assertOnShoppingCartPage() {
    Locator h1 = shop().locator("h1").first();
    h1.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    String title = h1.innerText().replaceAll("\\s+", " ").trim().toLowerCase();
    assertTrue(title.contains("shopping cart"),
        "Not on Shopping Cart page. Actual: " + title);
  }

  public void assertCartButtonsDisplayed() {
    // Scroll per te pare qe butonat jane te dukshme  
    Locator summary = shop().locator(".cart-summary").first();
    if (summary.count() > 0) summary.scrollIntoViewIfNeeded();

    Locator proceed = shop().locator(
        "a:has-text('Proceed to checkout'), " +
        "button:has-text('Proceed to checkout'), " +
        "a:has-text('Checkout'), " +
        "button:has-text('Checkout'), " +
        "a:has-text('PROCEED TO CHECKOUT'), " +
        "button:has-text('PROCEED TO CHECKOUT')"
    ).first();

    Locator continueShopping = shop().locator(
        "a:has-text('Continue shopping'), " +
        "button:has-text('Continue shopping'), " +
        "a:has-text('CONTINUE SHOPPING'), " +
        "button:has-text('CONTINUE SHOPPING')"
    ).first();

    proceed.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15_000));
    continueShopping.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15_000));

    assertTrue(proceed.isVisible(), "Proceed to checkout not visible");
    assertTrue(continueShopping.isVisible(), "Continue shopping not visible");
  }

  //  sum of items = total
  public void assertCartTotalIsCorrect() {
    Locator items = shop().locator(".cart-item");
    items.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15_000));

    double sum = 0.0;
    int n = items.count();
    for (int i = 0; i < n; i++) {
      String priceText = items.nth(i).locator(".product-price").first().innerText();
      sum += parseEuro(priceText);
    }

    Locator total = shop().locator(".cart-summary .cart-total .value").last();
    total.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15_000));
    double totalValue = parseEuro(total.innerText());

    assertEquals(sum, totalValue, 0.01,
        "Cart total mismatch. Sum=" + sum + " Total=" + totalValue);
  }

  private double parseEuro(String text) {
    String cleaned = text.replace("€", "")
        .replace("\u00A0", " ")
        .replace(",", ".")
        .replaceAll("[^0-9.]", "")
        .trim();
    return Double.parseDouble(cleaned);
  }
  
  
  
  
  
  
  
  
  
  
  
//================= TEST CASE 5: Empty Shopping Cart =================

public int getCartItemsCount() {
 return shop().locator(".cart-item").count();
}

public void deleteFirstItemFromCartAndAssertDecreased() {
 Locator items = shop().locator(".cart-item");
 items.first().waitFor(new Locator.WaitForOptions()
     .setState(WaitForSelectorState.VISIBLE)
     .setTimeout(15_000));

 int before = items.count();
 assertTrue(before > 0, "Cart is already empty, nothing to delete.");

 Locator firstItem = items.first();

 // robust remove selector (works across demo variants)
 Locator removeBtn = firstItem.locator(
     "a.remove-from-cart, " +
     "button.remove-from-cart, " +
     "a[data-link-action='delete-from-cart'], " +
     "button[data-link-action='delete-from-cart'], " +
     ".remove-from-cart"
 ).first();

 removeBtn.waitFor(new Locator.WaitForOptions()
     .setState(WaitForSelectorState.VISIBLE)
     .setTimeout(15_000));

 removeBtn.click(new Locator.ClickOptions().setForce(true));

 // wait until the cart-item count decreases
 waitUntil(() -> shop().locator(".cart-item").count() == before - 1, 15_000);

 int after = shop().locator(".cart-item").count();
 assertEquals(before - 1, after, "Cart items did not decrease by 1 after deletion.");
}

public void deleteAllItemsUntilCartEmpty() {
 while (shop().locator(".cart-item").count() > 0) {
   deleteFirstItemFromCartAndAssertDecreased();
 }
}

public void assertShoppingCartIsEmpty() {
	  // Primary: no cart items
	  waitUntil(() -> shop().locator(".cart-item").count() == 0, 15_000);
	  assertEquals(0, shop().locator(".cart-item").count(), "Cart is not empty (items still exist).");

	  // Secondary #1: Empty message on page (THIS matches your screenshot)
	  Locator emptyMsg = shop().locator(
	      "text=There are no more items in your cart," +
	      "text=There are no more items in your cart" +
	      "text=Your shopping cart is empty," +
	      "text=Your shopping cart is empty." +
	      ".cart-empty, .no-items"
	  ).first();

	  boolean emptyVisible = false;
	  try { emptyVisible = emptyMsg.isVisible(); } catch (Exception ignored) {}

	  // Secondary #2: Header shows Cart (0) - use broader selector
	  boolean headerCartIsZero = false;
	  try {
	    Locator headerCart = shop().locator("#_desktop_cart").first();
	    String header = headerCart.innerText().replaceAll("\\s+", " ").trim();
	    headerCartIsZero = header.contains("(0)") || header.contains("0 items") || header.contains("0");
	  } catch (Exception ignored) {}

	  assertTrue(
	      emptyVisible || headerCartIsZero,
	      "Cart is empty (0 items), but empty UI not detected. headerCartIsZero=" +
	          headerCartIsZero + ", emptyVisible=" + emptyVisible
	  );
	}


  
  
}








