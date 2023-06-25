package com.fabiano.bloomify.strategy;

public interface StorageStrategy {
  boolean contains(int element);

  void add(int hashCode);
}
