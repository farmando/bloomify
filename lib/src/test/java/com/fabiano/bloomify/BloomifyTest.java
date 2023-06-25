package com.fabiano.bloomify;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BloomifyTest {

  private Bloomify bloomify;

  @BeforeEach
  public void setUp() {
    bloomify = new Bloomify(1_000_000, 0.01);
  }

  @Test
  void bloomSense_canAddStringToSet() {
    var registeredEmail = "registered@email.com";
    var registeredEmail2 = "registered2@email.com";
    var registeredEmail3 = "registered3@email.com";

    bloomify.add(registeredEmail);
    bloomify.add(registeredEmail2);
    bloomify.add(registeredEmail3);

    assertTrue(bloomify.contains(registeredEmail));
    assertTrue(bloomify.contains(registeredEmail2));
    assertTrue(bloomify.contains(registeredEmail3));

    var registeredEmail4 = "registered4@email.com";
    assertFalse(bloomify.contains(registeredEmail4));
  }

  @Test
  void bloomSense_canAddListOfStringToSet() {
    var registeredEmail = "registered@email.com";
    var anotherRegisteredEmail = "another@email.com";
    List<String> registeredList = List.of(registeredEmail, anotherRegisteredEmail);

    bloomify.addAll(registeredList);

    await().atMost(150, MILLISECONDS).untilAsserted(() -> {
      assertTrue(bloomify.contains(registeredEmail));
      assertTrue(bloomify.contains(anotherRegisteredEmail));
    });
  }

  @Test
  void bloomSense_willThrowsExceptionWhenInvalidParameter() {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
            () -> new Bloomify(10000000, 0.00));

    Assertions.assertEquals("Invalid parameters", thrown.getMessage());
  }
}
