package com.fabiano.bloomify;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

  @ParameterizedTest
  @MethodSource("bloomifyArgsConstructor")
  void bloomSense_willThrowsExceptionWhenInvalidParameter(int expectedInsertions, double falsePositiveRate) {
    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
            () -> new Bloomify(expectedInsertions, falsePositiveRate));

    Assertions.assertEquals("Invalid parameters", thrown.getMessage());
  }

  private static Stream<Arguments> bloomifyArgsConstructor() {
    return Stream.of(
            arguments(1, 0.00),
            arguments(0, 0.01),
            arguments(1, 1.01)
    );
  }
}
