package com.fabiano.bloomify.strategy;

import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

public class LocalStorage implements StorageStrategy {
  private final BitSet bitSet;
  private final ReentrantLock lock;

  public LocalStorage(int size) {
    this.bitSet = new BitSet(size);
    this.lock = new ReentrantLock();
  }

  @Override
  public boolean contains(int element) {
    return bitSet.get(element);
  }

  @Override
  public void add(int hashCode) {
    lock.lock();
    try {
      bitSet.set(hashCode);
    } finally {
      lock.unlock();
    }
  }
}
