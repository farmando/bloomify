package com.fabiano.bloomify;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import com.fabiano.bloomify.strategy.LocalStorage;
import com.fabiano.bloomify.strategy.StorageStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Bloomify {
  private final StorageStrategy storageStrategy;
  private final int setSize;
  private final List<Function<String, Integer>> hashFunctions;

  public Bloomify(int expectedInsertions, double falsePositiveRate) {
    this(expectedInsertions, falsePositiveRate, new LocalStorage(expectedInsertions));
  }

  public Bloomify(int expectedInsertions, double falsePositiveRate, StorageStrategy strategy) {
    if (expectedInsertions <= 0 || falsePositiveRate <= 0 || falsePositiveRate >= 1) {
      throw new IllegalArgumentException("Invalid parameters");
    }

    this.setSize = calcSetSizeFor(expectedInsertions, falsePositiveRate);
    int numberOfHashFunctions = calcNumberOfHashFunctionsFor(expectedInsertions, setSize);
    this.storageStrategy = strategy;
    this.hashFunctions = generateHashFunctions(numberOfHashFunctions);
  }

  public boolean contains(String value) {
    return hashFunctions.stream().allMatch(function -> storageStrategy.contains(function.apply(value)));
  }

  public void add(String element) {
    hashFunctions.forEach(hash -> storageStrategy.add(hash.apply(element.trim())));
  }

  public void addAll(List<String> values) {
    int threadPoolSize = calcThreadPoolSize();
    int chunkSize = calcChunkSize(values, threadPoolSize);
    ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

    IntStream.range(0, threadPoolSize).forEach(t -> executor.execute(() -> {
      int fromIndex = t * chunkSize;
      int toIndex = Math.min(fromIndex + chunkSize, values.size());
      if (fromIndex < toIndex) {
        IntStream.range(fromIndex, toIndex).forEach(i -> add(values.get(i)));
      }
    }));
    executor.shutdown();
  }

  private List<Function<String, Integer>> generateHashFunctions(int numberOfHashFunctions) {
    List<Function<String, Integer>> functions = new ArrayList<>();
    IntStream.range(0, numberOfHashFunctions).forEach(seed -> {
      Function<String, Integer> hashFunction = element -> {
        AtomicInteger hashFactor = new AtomicInteger(0);
        element.chars().forEach(e -> hashFactor.set((hashFactor.incrementAndGet() * seed) + e));
        return Math.abs(hashFactor.get() % setSize);
      };
      functions.add(hashFunction);
    });
    return functions;
  }

  private int calcNumberOfHashFunctionsFor(int expectedInsertions, int size) {
    return max(1, (int) round(((double) size / expectedInsertions) * log(2)));
  }

  private int calcSetSizeFor(int expectedInsertions, double falsePositiveRate) {
    return (int) ceil((expectedInsertions * log(falsePositiveRate)) / log(1.0 / (pow(2.0, log(2.0)))));
  }

  private int calcThreadPoolSize() {
    //TODO - what is the best way to calculate the number of thread pool size
    int numOfCores = Runtime.getRuntime().availableProcessors();
    return numOfCores / 2 + 1;
  }

  private int calcChunkSize(List<String> values, int threadPoolSize) {
    int chunkSize = (values.size() / threadPoolSize);
    return chunkSize > 0 ? chunkSize : 1;
  }
}
