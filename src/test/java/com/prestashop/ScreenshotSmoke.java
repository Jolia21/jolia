package com.prestashop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScreenshotSmoke extends BaseTe {

  @Test
  void screenshot_smoke() {
    page.navigate("https://example.com");
    Assertions.fail("Smoke test – screenshot should be created");
  }
}
